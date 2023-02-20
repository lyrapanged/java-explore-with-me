package ru.practicum.explore.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.category.repository.CategoryRepository;
import ru.practicum.explore.event.dto.*;
import ru.practicum.explore.event.mapper.EventMapper;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.model.QEvent;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.explore.exception.BadValidationException;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.exception.WrongDateException;
import ru.practicum.explore.request.model.Request;
import ru.practicum.explore.request.repository.RequestRepository;
import ru.practicum.explore.user.model.User;
import ru.practicum.explore.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;
import static ru.practicum.explore.event.dto.EventDtoAdminUpdate.State.PUBLISH_EVENT;
import static ru.practicum.explore.event.dto.EventDtoAdminUpdate.State.REJECT_EVENT;
import static ru.practicum.explore.event.dto.EventDtoUpdate.State.CANCEL_REVIEW;
import static ru.practicum.explore.event.dto.EventDtoUpdate.State.SEND_TO_REVIEW;
import static ru.practicum.explore.event.model.Event.State;
import static ru.practicum.explore.event.model.Event.State.*;
import static ru.practicum.explore.request.model.Request.RequestStatus.CONFIRMED;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatsClient statsClient;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            EventMapper eventMapper, UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            RequestRepository requestRepository,
                            @Value("${stats-server.url}") String url,
                            @Value("${application.name}") String appName) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        statsClient = new StatsClient(url, appName, new RestTemplateBuilder());
    }

    @Override
    @Transactional
    public EventDto create(Long userId, EventDtoNew eventDtoNew) {
        if (eventDtoNew.getEventDate().isBefore(now())) {
            throw new WrongDateException("date should be in the future");
        }
        if (eventDtoNew.getAnnotation() == null || eventDtoNew.getAnnotation().isBlank()) {
            throw new BadValidationException("Wrong annotation");
        }
        User user = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(eventDtoNew.getCategory());
        Event newEventEntity = new Event(null, eventDtoNew.getAnnotation(), category, now(),
                eventDtoNew.getDescription(), eventDtoNew.getEventDate(), user, eventDtoNew.getLocation(),
                eventDtoNew.getPaid(), eventDtoNew.getParticipantLimit(), 0, null,
                eventDtoNew.getRequestModeration(), PENDING, eventDtoNew.getTitle(), 0L);
        return eventMapper.eventToEventDto(eventRepository.save(newEventEntity));
    }

    @Override
    public List<EventDtoShort> getAllByUser(Long userId, Pageable pageable) {
        getUserOrThrow(userId);
        return eventRepository.getAllByInitiatorId(userId, pageable).stream()
                .map(eventMapper::eventToEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventDto getByEventAndByUser(Long userId, Long eventId) {
        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);
        return eventMapper.eventToEventDto(event);
    }

    @Override
    @Transactional
    public EventDto updateByUser(Long userId, Long eventId, EventDtoUpdate eventDtoUpdate) {
        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);
        if (eventDtoUpdate.getEventDate() != null && eventDtoUpdate.getEventDate().isBefore(now())) {
            throw new WrongDateException("Data should be greater than now");
        }
        if (event.getState().equals(PUBLISHED)) {
            throw new WrongDateException("Wrong state");
        }
        Event eventForUpdate = eventMapper.updateEventWithUser(eventDtoUpdate, event);
        if (eventDtoUpdate.getCategory() != null) {
            Category category = getCategoryOrThrow(eventDtoUpdate.getCategory());
            eventForUpdate.setCategory(category);
        }
        if (CANCEL_REVIEW.equals(eventDtoUpdate.getStateAction())) {
            eventForUpdate.setState(CANCELED);
        }
        if (SEND_TO_REVIEW.equals(eventDtoUpdate.getStateAction())) {
            eventForUpdate.setState(PENDING);
        }
        return eventMapper.eventToEventDto(eventRepository.save(eventForUpdate));
    }

    @Override
    @Transactional
    public EventDto updateByAdmin(Long eventId, EventDtoAdminUpdate eventDtoAdminUpdate) {
        Event event = getEventOrThrow(eventId);
        if (!event.getState().equals(PENDING)) {
            throw new WrongDateException("Wrong state");
        }
        if (eventDtoAdminUpdate.getEventDate() != null && eventDtoAdminUpdate.getEventDate().isBefore(now())) {
            throw new WrongDateException("Data should be in the future");
        }
        Event eventForUpdate = eventMapper.updateEventWithUser(eventDtoAdminUpdate, event);
        if (eventDtoAdminUpdate.getCategory() != null) {
            Category category = getCategoryOrThrow(eventDtoAdminUpdate.getCategory());
            eventForUpdate.setCategory(category);
        }
        if (REJECT_EVENT.equals(eventDtoAdminUpdate.getStateAction())) {
            eventForUpdate.setState(CANCELED);
        }
        if (PUBLISH_EVENT.equals(eventDtoAdminUpdate.getStateAction())) {
            eventForUpdate.setState(PUBLISHED);
            eventForUpdate.setPublishedOn(now());
        }
        return eventMapper.eventToEventDto(eventRepository.save(eventForUpdate));
    }

    @Override
    public EventDtoFull get(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, PUBLISHED);
        statsClient.create(request);
        EventDtoFull eventFullDto = eventMapper.eventToEventFullDto(event);
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        return addStats(eventFullDto);
    }

    @Override
    public List<EventDtoFull> getAllByFilter(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Pageable pageable, HttpServletRequest request) {
        statsClient.create(request);
        Predicate predicate = predicateByUser(text, categories, paid, rangeStart,
                rangeEnd).getValue();
        List<EventDtoFull> eventDtoFulls = eventRepository.findAll(requireNonNull(predicate), pageable).getContent()
                .stream()
                .map(eventMapper::eventToEventFullDto)
                .collect(Collectors.toList());
        return eventDtoFulls.stream().map(this::addStats).collect(Collectors.toList());
    }

    @Override
    public List<EventDtoFull> getAllByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        Predicate predicate = predicateByAdmin(users, states, categories, rangeStart, rangeEnd).getValue();
        List<EventDtoFull> eventDtoFulls = eventRepository.findAll(requireNonNull(predicate), pageable).getContent()
                .stream()
                .map(eventMapper::eventToEventFullDto)
                .collect(Collectors.toList());
        return eventDtoFulls.stream().map(this::addStats).collect(Collectors.toList());
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("User id = %s not found", id)));
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Category id = %s not found", id)));
    }

    private Event getEventOrThrow(Long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Event id = %s not found", id)));
    }

    private BooleanBuilder predicateByAdmin(List<Long> users, List<State> states,
                                            List<Long> categories, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QEvent qEvent = QEvent.event;
        if (users != null) {
            booleanBuilder.and(QEvent.event.initiator.id.in(users));
        }
        if (categories != null) {
            booleanBuilder.and(QEvent.event.category.id.in(categories));
        }
        if (states != null) {
            booleanBuilder.and(QEvent.event.state.in(states));
        }

        if (rangeEnd != null) {
            booleanBuilder.and(qEvent.eventDate.before(rangeEnd));
        }
        if (rangeStart != null) {
            booleanBuilder.and(qEvent.eventDate.after(rangeStart));
        }
        if (rangeStart == null) {
            booleanBuilder.and(qEvent.eventDate.after(now()));
        }
        return booleanBuilder;
    }

    private BooleanBuilder predicateByUser(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QEvent qEvent = QEvent.event;
        booleanBuilder.and(qEvent.state.eq(PUBLISHED));
        if (text != null) {
            booleanBuilder.and(qEvent.annotation.containsIgnoreCase(text).or(qEvent.description.containsIgnoreCase(text)));
        }
        if (paid != null) {
            booleanBuilder.and(QEvent.event.paid.eq(paid));
        }
        if (rangeStart == null) {
            booleanBuilder.and(qEvent.eventDate.after(now()));
        }
        if (rangeStart != null) {
            booleanBuilder.and(qEvent.eventDate.after(rangeStart));
        }
        if (categories != null) {
            booleanBuilder.and(QEvent.event.category.id.in(categories));
        }

        if (rangeEnd != null) {
            booleanBuilder.and(qEvent.eventDate.before(rangeEnd));
        }
        return booleanBuilder;
    }

    private EventDtoFull addStats(EventDtoFull eventDtoFull) {
        List<ViewStatsDto> stat =
                statsClient.get(eventDtoFull.getCreatedOn().format(FORMATTER),
                        now().format(FORMATTER),
                        List.of("/events/" + eventDtoFull.getId()), false).getBody();
        if (stat == null) {
            stat = List.of();
        }
        if (!stat.isEmpty()) {
            eventDtoFull.setViews(stat.get(0).getHits());
        }
        List<Request> requestList = requestRepository.findAllByStatusAndAndEventId(CONFIRMED, eventDtoFull.getId());
        eventDtoFull.setConfirmedRequests(requestList.size());
        return eventDtoFull;
    }
}

package ru.practicum.explore.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.category.repository.CategoryRepository;
import ru.practicum.explore.event.dto.*;
import ru.practicum.explore.event.mapper.EventMapper;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.model.QEvent;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.exception.WrongDateException;
import ru.practicum.explore.statistics.StatisticsService;
import ru.practicum.explore.user.model.User;
import ru.practicum.explore.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatisticsService statisticsService;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            EventMapper eventMapper, UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            StatisticsService statisticsService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.statisticsService = statisticsService;
    }

    @Override
    @Transactional
    public EventDto create(Long userId, EventDtoNew eventDtoNew) {
        if (eventDtoNew.getEventDate().isBefore(now())) {
            throw new WrongDateException("date should be in the future");
        }
        User user = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(eventDtoNew.getCategory());
        Event newEventEntity = new Event(null, eventDtoNew.getAnnotation(), category, now(),
                eventDtoNew.getDescription(), eventDtoNew.getEventDate(), user, eventDtoNew.getLocation(),
                eventDtoNew.getPaid(), eventDtoNew.getParticipantLimit(), null,
                eventDtoNew.getRequestModeration(), PENDING, eventDtoNew.getTitle());
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
        Event eventForUpdate = updateEvent(event, eventDtoUpdate);

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
        Event eventForUpdate = updateAdminEvent(event, eventDtoAdminUpdate);
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
        statisticsService.create(request);
        EventDtoFull eventFullDto = eventMapper.eventToEventFullDto(event);
        List<EventDtoFull> eventDtoFulls = new ArrayList<>();
        eventDtoFulls.add(eventFullDto);
        eventRepository.save(event);
        List<EventDtoFull> eventDtoFullsReturned = statisticsService.addStats(eventDtoFulls);
        if (eventDtoFullsReturned.size() != 0) {
            return eventDtoFullsReturned.get(0);
        } else {
            return eventFullDto;
        }

    }

    @Override
    public List<EventDtoFull> getAllByFilter(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Pageable pageable, HttpServletRequest request) {
        statisticsService.create(request);
        Predicate predicate = predicateByUser(text, categories, paid, rangeStart,
                rangeEnd).getValue();
        List<EventDtoFull> eventDtoFulls = eventRepository.findAll(requireNonNull(predicate), pageable).getContent()
                .stream()
                .map(eventMapper::eventToEventFullDto)
                .collect(Collectors.toList());
        return statisticsService.addStats(eventDtoFulls);
    }

    @Override
    public List<EventDtoFull> getAllByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        Predicate predicate = predicateByAdmin(users, states, categories, rangeStart, rangeEnd).getValue();
        List<EventDtoFull> eventDtoFulls = eventRepository.findAll(requireNonNull(predicate), pageable).getContent()
                .stream()
                .map(eventMapper::eventToEventFullDto)
                .collect(Collectors.toList());
        return statisticsService.addStats(eventDtoFulls);
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

    private Event updateAdminEvent(Event event, EventDtoAdminUpdate eventDtoAdminUpdate) {
        if (eventDtoAdminUpdate.getTitle() != null && !(eventDtoAdminUpdate.getTitle().isBlank())) {
            event.setTitle(eventDtoAdminUpdate.getTitle());
        }
        if (eventDtoAdminUpdate.getAnnotation() != null && !(eventDtoAdminUpdate.getAnnotation().isBlank())) {
            event.setAnnotation(eventDtoAdminUpdate.getAnnotation());
        }
        if (eventDtoAdminUpdate.getDescription() != null && !(eventDtoAdminUpdate.getDescription().isBlank())) {
            event.setDescription(eventDtoAdminUpdate.getDescription());
        }
        if (eventDtoAdminUpdate.getEventDate() != null) {
            event.setEventDate(eventDtoAdminUpdate.getEventDate());
        }
        if (eventDtoAdminUpdate.getLocation() != null) {
            event.setLocation(event.getLocation());
        }
        if (eventDtoAdminUpdate.getPaid() != null) {
            event.setPaid(eventDtoAdminUpdate.getPaid());
        }
        if (eventDtoAdminUpdate.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDtoAdminUpdate.getParticipantLimit());
        }
        if (eventDtoAdminUpdate.getRequestModeration() != null) {
            event.setRequestModeration(eventDtoAdminUpdate.getRequestModeration());
        }
        return event;
    }

    private Event updateEvent(Event event, EventDtoUpdate eventDtoUpdate) {
        if (eventDtoUpdate.getTitle() != null && !(eventDtoUpdate.getTitle().isBlank())) {
            event.setTitle(eventDtoUpdate.getTitle());
        }
        if (eventDtoUpdate.getAnnotation() != null && !(eventDtoUpdate.getAnnotation().isBlank())) {
            event.setAnnotation(eventDtoUpdate.getAnnotation());
        }
        if (eventDtoUpdate.getDescription() != null && !(eventDtoUpdate.getDescription().isBlank())) {
            event.setDescription(eventDtoUpdate.getDescription());
        }
        if (eventDtoUpdate.getEventDate() != null) {
            event.setEventDate(eventDtoUpdate.getEventDate());
        }
        if (eventDtoUpdate.getLocation() != null) {
            event.setLocation(event.getLocation());
        }
        if (eventDtoUpdate.getPaid() != null) {
            event.setPaid(eventDtoUpdate.getPaid());
        }
        if (eventDtoUpdate.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDtoUpdate.getParticipantLimit());
        }
        if (eventDtoUpdate.getRequestModeration() != null) {
            event.setRequestModeration(eventDtoUpdate.getRequestModeration());
        }
        return event;
    }
}

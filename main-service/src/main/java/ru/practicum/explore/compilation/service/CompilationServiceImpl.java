package ru.practicum.explore.compilation.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.dto.CompilationDtoNew;
import ru.practicum.explore.compilation.dto.CompilationDtoUpdate;
import ru.practicum.explore.compilation.model.Compilation;
import ru.practicum.explore.compilation.model.QCompilation;
import ru.practicum.explore.compilation.repository.CompilationRepository;
import ru.practicum.explore.event.dto.EventDtoCompilation;
import ru.practicum.explore.event.dto.EventDtoFull;
import ru.practicum.explore.event.mapper.EventMapper;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.request.model.Request;
import ru.practicum.explore.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.explore.request.model.Request.RequestStatus.CONFIRMED;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;
    private final StatsClient statsClient;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public CompilationServiceImpl(EventRepository eventRepository,
                                  EventMapper eventMapper, RequestRepository requestRepository,
                                  CompilationRepository compilationRepository,
                                  @Value("${stats-server.url}") String url,
                                  @Value("${application.name}") String appName) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.requestRepository = requestRepository;
        this.compilationRepository = compilationRepository;
        this.statsClient = new StatsClient(url, appName, new RestTemplateBuilder());
    }

    @Override
    public CompilationDto create(CompilationDtoNew compilationDtoNew) {
        List<Event> events = eventRepository.findAllByIdIn(compilationDtoNew.getEvents());
        Compilation compilation = new Compilation(null, events, compilationDtoNew.isPinned(),
                compilationDtoNew.getTitle());
        return createCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto update(Long compId, CompilationDtoUpdate compilationDtoUpdate) {
        Compilation compilation = getCompilationOrThrow(compId);
        if (compilationDtoUpdate.getTitle() != null) {
            compilation.setTitle(compilationDtoUpdate.getTitle());
        }
        if (compilationDtoUpdate.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(compilationDtoUpdate.getEvents()));
        }
        if (compilationDtoUpdate.getPinned() != null) {
            compilation.setPinned(compilationDtoUpdate.getPinned());
        }
        compilationRepository.save(compilation);
        return createCompilationDto(compilation);
    }

    @Override
    public CompilationDto get(Long compId) {
        Compilation compilation = getCompilationOrThrow(compId);
        return createCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        QCompilation qCompilation = QCompilation.compilation;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qCompilation.pinned.eq(true));
        if (pinned != null) {
            booleanBuilder.and(qCompilation.pinned.eq(pinned));
        }
        return compilationRepository.findAll(Objects.requireNonNull(booleanBuilder.getValue()),
                        pageable).getContent()
                .stream()
                .map(this::createCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long compId) {
        getCompilationOrThrow(compId);
        compilationRepository.deleteById(compId);
    }

    private Compilation getCompilationOrThrow(Long id) {
        return compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Compilation id = %s not found", id)));
    }

    private CompilationDto createCompilationDto(Compilation compilation) {
        List<EventDtoFull> eventFullDtoList = compilation.getEvents()
                .stream()
                .map(eventMapper::eventToEventFullDto)
                .collect(Collectors.toList());
        List<EventDtoCompilation> eventDtoCompilationList = eventFullDtoList
                .stream()
                .map(this::addStats)
                .collect(Collectors.toList());
        return new CompilationDto(eventDtoCompilationList, compilation.getId(),
                compilation.isPinned(), compilation.getTitle());
    }

    private EventDtoCompilation addStats(EventDtoFull eventDtoFull) {
        List<ViewStatsDto> stat = statsClient.get(eventDtoFull.getCreatedOn().format(FORMATTER),
                LocalDateTime.now().format(FORMATTER),
                List.of("/events/" + eventDtoFull.getId()), false).getBody();
        if (stat == null) {
            stat = List.of();
        }
        if (!stat.isEmpty()) {
            eventDtoFull.setViews(stat.get(0).getHits());
        }
        List<Request> confirmedRequests = requestRepository.findAllByStatusAndAndEventId(CONFIRMED,
                eventDtoFull.getId());
        eventDtoFull.setConfirmedRequests(confirmedRequests.size());
        return eventMapper.eventDtoFullToCompilationDto(eventDtoFull);
    }
}

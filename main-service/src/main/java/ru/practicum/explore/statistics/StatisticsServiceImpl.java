package ru.practicum.explore.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.explore.event.dto.EventDtoFull;
import ru.practicum.explore.request.model.Request;
import ru.practicum.explore.request.repository.RequestRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.explore.request.model.Request.RequestStatus.CONFIRMED;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatisticsServiceImpl(
            RequestRepository requestRepository,
            @Value("${stats-server.url}") String url,
            @Value("${application.name}") String appName) {
        this.requestRepository = requestRepository;
        statsClient = new StatsClient(url, appName, new RestTemplateBuilder());
    }

    @Override
    public List<EventDtoFull> addStats(List<EventDtoFull> eventDtoFulls) {
        if (!eventDtoFulls.isEmpty()) {
            List<Long> eventIds = eventDtoFulls.stream()
                    .map(EventDtoFull::getId)
                    .collect(Collectors.toList());
            List<String> uris = eventDtoFulls
                    .stream()
                    .map(eventDtoFull -> "/events/" + eventDtoFull.getId())
                    .collect(Collectors.toList());
            List<ViewStatsDto> stat = statsClient.get(LocalDateTime.now().minusYears(30).format(FORMATTER),
                    LocalDateTime.now().plus(30, ChronoUnit.YEARS).format(FORMATTER),
                    uris, false).getBody();
            if (stat.size() == 0) {
                stat = List.of();
            }
            Map<String, List<ViewStatsDto>> appKeyViewStatusDto = stat.stream()
                    .collect(Collectors.groupingBy(ViewStatsDto::getUri));
            if (!stat.isEmpty()) {
                eventDtoFulls.forEach(eventDtoFull -> eventDtoFull
                        .setViews(appKeyViewStatusDto.get("/events/" + eventDtoFull.getId()).get(0).getHits()));
            }
            List<Request> confirmedRequests = requestRepository.findAllByStatusAndEventIdIn(CONFIRMED, eventIds);
            Map<Long, Long> requestsWithId = confirmedRequests
                    .stream()
                    .collect(Collectors.groupingBy((Request::getId), Collectors.counting()));
            if (requestsWithId.size() != 0) {
                eventDtoFulls.forEach(eventDtoFull -> eventDtoFull
                        .setConfirmedRequest(requestsWithId.get(eventDtoFull.getId()).intValue()));
            }
            return eventDtoFulls;
        }
        return List.of();
    }

    @Override
    public void create(HttpServletRequest request) {
        statsClient.create(request);
    }
}

package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stat.mapper.ViewStatsMapper;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.model.ViewStats;
import ru.practicum.stat.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.stat.util.Constant.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    private final ViewStatsMapper viewStatsMapper;


    @Override
    @Transactional
    public void create(EndpointHit endpointHit) {
        statRepository.save(endpointHit);
    }

    @Override
    public List<ViewStatsDto> get(String start, String end, List<String> uris, boolean unique) {

        if (unique) {
            return mapper(statRepository.getUnique(
                    LocalDateTime.parse(start, DATE_TIME_FORMATTER),
                    LocalDateTime.parse(end, DATE_TIME_FORMATTER), uris));
        } else {
            return mapper(statRepository.get(
                    LocalDateTime.parse(start, DATE_TIME_FORMATTER),
                    LocalDateTime.parse(end, DATE_TIME_FORMATTER), uris));
        }
    }

    private List<ViewStatsDto> mapper(List<ViewStats> collection) {
        if (!collection.isEmpty()) {
            return collection.stream().map(viewStatsMapper::viewStatsToViewStatsDto).collect(Collectors.toList());
        } else {
            return List.of();
        }
    }
}

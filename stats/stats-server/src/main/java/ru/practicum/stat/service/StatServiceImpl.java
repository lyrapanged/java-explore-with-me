package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stat.mapper.ViewStatsMapper;
import ru.practicum.stat.model.App;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.model.ViewStats;
import ru.practicum.stat.repository.AppRepository;
import ru.practicum.stat.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    private final ViewStatsMapper viewStatsMapper;
    private final AppRepository appRepository;


    @Override
    @Transactional
    public void create(EndpointHit endpointHit) {
        String name = endpointHit.getApp().getName();
        Optional<App> appOptional = appRepository.findByName(name);
        App app = new App();
        if (appOptional.isEmpty()) {
            app.setName(name);
            appRepository.save(app);
        } else {
            app = appOptional.get();
        }
        endpointHit.setApp(app);
        statRepository.save(endpointHit);
    }

    @Override
    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            return mapper(statRepository.getUnique(start, end, uris));
        } else {
            return mapper(statRepository.get(start, end, uris));
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

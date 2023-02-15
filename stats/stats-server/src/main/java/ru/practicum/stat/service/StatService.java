package ru.practicum.stat.service;

import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stat.model.EndpointHit;

import java.util.List;

public interface StatService {

    void create(EndpointHit endpointHit);

    List<ViewStatsDto> get(String start, String end, List<String> uris, boolean unique);
}

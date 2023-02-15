package ru.practicum.stat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stat.mapper.EndpointHitMapper;
import ru.practicum.stat.service.StatService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {

    private final StatService statService;
    private final EndpointHitMapper endpointHitMapper;

    @PostMapping("hit")
    public void create(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.debug("Saving hit {}", endpointHitDto.getApp());
        statService.create(endpointHitMapper.endpointHitDtoToEndpoint(endpointHitDto));
    }

    @GetMapping("stats")
    public List<ViewStatsDto> get(@RequestParam List<String> uris,
                                  @RequestParam(required = false) boolean unique,
                                  @RequestParam String start,
                                  @RequestParam String end) {
        log.debug("Getting stats");
        return statService.get(start, end, uris, unique);
    }
}

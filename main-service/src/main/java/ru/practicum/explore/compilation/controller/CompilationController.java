package ru.practicum.explore.compilation.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.service.CompilationServiceImpl;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/compilations")
public class CompilationController {

    private final CompilationServiceImpl compilationService;

    @GetMapping("{compId}")
    public CompilationDto get(@PositiveOrZero @PathVariable Long compId) {
        log.info("Getting compilation id = {}", compId);
        return compilationService.get(compId);
    }

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all compilations with pinned = {}", pinned);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return compilationService.getAll(pinned, pageable);
    }
}

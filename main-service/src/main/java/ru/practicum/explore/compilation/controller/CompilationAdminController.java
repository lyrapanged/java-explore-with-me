package ru.practicum.explore.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.dto.CompilationDtoNew;
import ru.practicum.explore.compilation.dto.CompilationDtoUpdate;
import ru.practicum.explore.compilation.service.CompilationServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {

    private final CompilationServiceImpl compilationService;

    @PostMapping
    @ResponseStatus(CREATED)
    public CompilationDto create(@RequestBody @Valid CompilationDtoNew compilationDtoNew) {
        log.info("Creating compilation {}", compilationDtoNew.getTitle());
        return compilationService.create(compilationDtoNew);
    }

    @PatchMapping("{compId}")
    public CompilationDto update(@Positive @PathVariable Long compId,
                                 @RequestBody CompilationDtoUpdate compilationDtoUpdate) {
        log.info("Updating compilation compId = {}", compId);
        return compilationService.update(compId, compilationDtoUpdate);
    }

    @DeleteMapping("{compId}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@Positive @PathVariable Long compId) {
        log.info("Deleting compId = {}", compId);
        compilationService.delete(compId);
    }
}

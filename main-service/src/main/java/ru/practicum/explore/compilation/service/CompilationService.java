package ru.practicum.explore.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.dto.CompilationDtoNew;
import ru.practicum.explore.compilation.dto.CompilationDtoUpdate;

import java.util.List;

public interface CompilationService {

    CompilationDto create(CompilationDtoNew compilationDtoNew);

    CompilationDto update(Long compId, CompilationDtoUpdate compilationDtoUpdate);

    CompilationDto get(Long compId);

    List<CompilationDto> getAll(Boolean pinned, Pageable pageable);

    void delete(Long compId);
}

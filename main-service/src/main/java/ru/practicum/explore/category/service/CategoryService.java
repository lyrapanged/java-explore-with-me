package ru.practicum.explore.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.category.dto.CategoryDtoShort;

import java.util.List;

public interface CategoryService {

    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(Long id, CategoryDtoShort categoryDtoShort);

    CategoryDto get(Long id);

    List<CategoryDto> getAll(Pageable pageable);

    void delete(Long id);
}

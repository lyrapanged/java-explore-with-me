package ru.practicum.explore.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.category.dto.CategoryDtoShort;
import ru.practicum.explore.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(CREATED)
    public CategoryDto create(@RequestBody @Valid CategoryDto category) {
        log.info("Creating category {}", category.getName());
        return categoryService.create(category);
    }

    @PatchMapping("{catId}")
    public CategoryDto update(@PathVariable("catId") Long id, @RequestBody @Valid CategoryDtoShort categoryDtoShort) {
        log.info("Update category  id = {}", id);
        return categoryService.update(id, categoryDtoShort);
    }

    @DeleteMapping("{catId}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@Positive @PathVariable("catId") Long id) {
        log.info("Deleting category id = {}", id);
        categoryService.delete(id);
    }
}

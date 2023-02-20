package ru.practicum.explore.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("{catId}")
    public CategoryDto get(@PathVariable("catId") Long id) {
        log.info("Get category with id = {}", id);
        return categoryService.get(id);
    }

    @GetMapping
    public List<CategoryDto> getAll(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                    @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Getting categories");
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryService.getAll(pageable);
    }
}

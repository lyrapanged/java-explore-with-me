package ru.practicum.explore.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.category.dto.CategoryDtoShort;
import ru.practicum.explore.category.mapper.CategoryMapper;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.category.repository.CategoryRepository;
import ru.practicum.explore.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto create(CategoryDto categoryDto) {
        Category category = categoryMapper.categoryDtoToCategory(categoryDto);
        return categoryMapper.categoryToCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryDtoShort categoryDtoShort) {
        Category category = getCategoryOrThrow(id);
        categoryMapper.updateCategory(categoryDtoShort, category);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.categoryToCategoryDto(updatedCategory);
    }

    @Override
    public CategoryDto get(Long id) {
        return categoryMapper.categoryToCategoryDto(getCategoryOrThrow(id));
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .stream()
                .map(categoryMapper::categoryToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getCategoryOrThrow(id);
        categoryRepository.deleteById(id);
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Category id = %s not found", id)));
    }
}

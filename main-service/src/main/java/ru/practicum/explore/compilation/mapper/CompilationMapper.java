package ru.practicum.explore.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.explore.category.mapper.CategoryMapper;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.model.Compilation;
import ru.practicum.explore.event.mapper.EventMapper;
import ru.practicum.explore.user.mapper.UserMapper;

@Mapper(componentModel = "spring",
        uses = {
                UserMapper.class,
                CategoryMapper.class,
                EventMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    CompilationDto compilationToCompilationDto(Compilation compilation);
}

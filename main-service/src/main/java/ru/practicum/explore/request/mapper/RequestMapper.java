package ru.practicum.explore.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.explore.category.mapper.CategoryMapper;
import ru.practicum.explore.event.mapper.EventMapper;
import ru.practicum.explore.request.dto.RequestDto;
import ru.practicum.explore.request.model.Request;
import ru.practicum.explore.user.mapper.UserMapper;

@Mapper(componentModel = "spring",
        uses = {
                UserMapper.class,
                CategoryMapper.class,
                EventMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RequestMapper {
    @Mapping(target = "event.id", source = "event")
    @Mapping(target = "requester.id", source = "requester")
    Request requestDtoToRequest(RequestDto requestDto);


    @Mapping(source = "requester.id", target = "requester")
    @Mapping(source = "event.id", target = "event")
    RequestDto requestToRequestDto(Request request);
}

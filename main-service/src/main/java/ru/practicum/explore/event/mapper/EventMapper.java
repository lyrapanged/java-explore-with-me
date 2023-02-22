package ru.practicum.explore.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.explore.category.mapper.CategoryMapper;
import ru.practicum.explore.event.dto.*;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.model.Location;
import ru.practicum.explore.user.mapper.UserMapper;

@Mapper(componentModel = "spring",
        uses = {
                UserMapper.class,
                CategoryMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "category.id", source = "category")
    Event eventDtoNewtoEvent(EventDtoNew eventDtoNew);

    EventDtoCompilation eventDtoFullToCompilationDto(EventDtoFull eventDtoFull);

    EventDtoShort eventToEventShortDto(Event event);

    EventDto eventToEventDto(Event event);

    EventDtoFull eventToEventFullDto(Event event);

    Location toEntity(Location location);
}

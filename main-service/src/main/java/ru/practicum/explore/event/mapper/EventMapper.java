package ru.practicum.explore.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "category", ignore = true)
    Event updateEventWithUser(EventDtoUpdate eventUpdateDto, @MappingTarget Event event);

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "category", ignore = true)
    Event updateEventWithUser(EventDtoAdminUpdate eventUpdateAdminDto, @MappingTarget Event event);

}

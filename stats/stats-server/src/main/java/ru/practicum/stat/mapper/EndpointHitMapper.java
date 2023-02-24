package ru.practicum.stat.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.stat.model.App;
import ru.practicum.stat.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    @Mapping(target = "app", source = "app.name")
    EndpointHitDto endpointHitToEndpointDto(EndpointHit endpointHit);

    @Mapping(target = "app", source = "app")
    EndpointHit endpointHitDtoToEndpoint(EndpointHitDto endpointHitDto);

    @Mapping(target = "id", ignore = true)
    App appDtoToApp(String name);
}

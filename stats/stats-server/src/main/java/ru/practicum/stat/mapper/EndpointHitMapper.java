package ru.practicum.stat.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.stat.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    EndpointHitDto endpointHitToEndpointDto(EndpointHit endpointHit);

    EndpointHit endpointHitDtoToEndpoint(EndpointHitDto endpointHitDto);
}

package ru.practicum.stat.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stat.model.ViewStats;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {

    ViewStatsDto viewStatsToViewStatsDto(ViewStats viewStats);

    ViewStats viewStatsDtoToViewStats(ViewStatsDto viewStatsDto);

}

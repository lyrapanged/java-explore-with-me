package ru.practicum.explore.statistics;

import ru.practicum.explore.event.dto.EventDtoFull;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StatisticsService {

    List<EventDtoFull> addStats(List<EventDtoFull> eventDtoFulls);

    void create(HttpServletRequest request);
}

package ru.practicum.explore.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.explore.event.dto.*;
import ru.practicum.explore.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventDto create(Long userId, EventDtoNew eventDtoNew);

    EventDto updateByUser(Long userId, Long eventId, EventDtoUpdate eventDtoUpdate);

    EventDto updateByAdmin(Long eventId, EventDtoAdminUpdate eventDtoAdminUpdate);

    EventDtoFull get(Long id, HttpServletRequest request);

    List<EventDtoShort> getAllByUser(Long userId, Pageable pageable);

    EventDto getByEventAndByUser(Long userId, Long eventId);

    List<EventDtoFull> getAllByFilter(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, Pageable pageable, HttpServletRequest request);

    List<EventDtoFull> getAllByAdmin(List<Long> users, List<Event.State> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);
}

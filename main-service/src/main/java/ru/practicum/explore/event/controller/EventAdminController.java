package ru.practicum.explore.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.dto.EventDto;
import ru.practicum.explore.event.dto.EventDtoAdminUpdate;
import ru.practicum.explore.event.dto.EventDtoFull;
import ru.practicum.explore.event.model.Event.State;
import ru.practicum.explore.event.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventService eventService;

    @PatchMapping("{eventId}")
    public EventDto update(@PathVariable Long eventId,
                           @RequestBody EventDtoAdminUpdate eventDtoAdminUpdate) {
        log.info("Updating event id = {}", eventId);
        return eventService.updateByAdmin(eventId, eventDtoAdminUpdate);
    }

    @GetMapping
    public List<EventDtoFull> getAll(@RequestParam(required = false) List<Long> users,
                                     @RequestParam(required = false) List<State> states,
                                     @RequestParam(required = false) List<Long> categories,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                     LocalDateTime rangeStart,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                     LocalDateTime rangeEnd,
                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all events");
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return eventService.getAllByAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
    }
}

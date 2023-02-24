package ru.practicum.explore.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.dto.EventDto;
import ru.practicum.explore.event.dto.EventDtoNew;
import ru.practicum.explore.event.dto.EventDtoShort;
import ru.practicum.explore.event.dto.EventDtoUpdate;
import ru.practicum.explore.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users")
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping("{userId}/events")
    @ResponseStatus(CREATED)
    public EventDto create(@PathVariable("userId") Long id, @RequestBody @Valid EventDtoNew eventDtoNew) {
        log.info("Creating event id = {}", eventDtoNew.getCategory());
        return eventService.create(id, eventDtoNew);
    }

    @PatchMapping("{userId}/events/{eventId}")
    public EventDto updateByUser(@PathVariable Long userId, @PathVariable Long eventId,
                                 @RequestBody @Valid EventDtoUpdate eventDtoUpdate) {
        log.info("Updating event id = {}", eventId);
        return eventService.updateByUser(userId, eventId, eventDtoUpdate);
    }

    @GetMapping("{userId}/events/{eventId}")
    public EventDto getByEventAndByUser(@PathVariable("userId") Long id, @PathVariable Long eventId) {
        log.info("Getting events with id = {}", id);
        return eventService.getByEventAndByUser(id, eventId);
    }

    @GetMapping("{userId}/events")
    public List<EventDtoShort> getAllByUser(@PathVariable("userId") Long id,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                            @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Getting events with userID = {}", id);
        Pageable pageable = PageRequest.of(from / size, size);
        return eventService.getAllByUser(id, pageable);
    }
}

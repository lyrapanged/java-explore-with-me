package ru.practicum.explore.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.dto.EventDtoFull;
import ru.practicum.explore.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static ru.practicum.explore.event.controller.EventController.FilterSort.EVENT_DATE;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @GetMapping("{id}")
    public EventDtoFull get(@Positive @PathVariable Long id, HttpServletRequest request) {
        log.info("Getting event id = {}", id);
        return eventService.get(id, request);
    }

    @GetMapping
    public List<EventDtoFull> getAll(@RequestParam(required = false) String text,
                                     @RequestParam(required = false) List<Long> categories,
                                     @RequestParam(required = false) Boolean paid,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                     @RequestParam(required = false, defaultValue = "EVENT_DATE") FilterSort sort,
                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                     @Positive @RequestParam(defaultValue = "10") Integer size, HttpServletRequest request) {
        log.info("Getting evens");
        Pageable pageable;
        if (sort.equals(EVENT_DATE)) {
            pageable = PageRequest.of(from / size, size, Sort.by(ASC, "eventDate"));
        } else {
            pageable = PageRequest.of(from / size, size, Sort.by(ASC, "views"));
        }
        return eventService.getAllByFilter(text, categories, paid, rangeStart, rangeEnd, pageable, request);
    }

    public enum FilterSort {
        VIEWS, EVENT_DATE
    }
}

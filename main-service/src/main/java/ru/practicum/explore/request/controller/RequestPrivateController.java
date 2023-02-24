package ru.practicum.explore.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.request.dto.RequestDto;
import ru.practicum.explore.request.dto.RequestDtoCollection;
import ru.practicum.explore.request.dto.RequestDtoStatusUpdate;
import ru.practicum.explore.request.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users")
public class RequestPrivateController {

    private final RequestService requestService;

    @PostMapping("{userId}/requests")
    @ResponseStatus(CREATED)
    public RequestDto create(@Positive @PathVariable Long userId, @Positive @RequestParam Long eventId) {
        log.info("Adding user userID{} in event eventID{}", userId, eventId);
        return requestService.create(userId, eventId);
    }

    @PatchMapping("{userId}/events/{eventId}/requests")
    public RequestDtoCollection updateState(@PathVariable Long userId, @PathVariable Long eventId,
                                            @RequestBody RequestDtoStatusUpdate requestDtoStatusUpdate) {
        log.info("Updating status requests with eventID = {} and userID = {}", eventId, userId);
        return requestService.updateState(eventId, userId, requestDtoStatusUpdate);
    }

    @PatchMapping("{userId}/requests/{requestId}/cancel")
    public RequestDto updateStateCancel(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Cancelling own status requests with userID = {} and requestID = {}", userId, requestId);
        return requestService.updateStateCancel(userId, requestId);
    }

    @GetMapping("{userId}/requests")
    public List<RequestDto> get(@PathVariable Long userId) {
        log.info("Getting request by userId = {}", userId);
        return requestService.get(userId);
    }

    @GetMapping("{userId}/events/{eventId}/requests")
    public List<RequestDto> getAllByOwner(@PathVariable Long eventId, @PathVariable Long userId) {
        log.info("Getting requests with eventID = {} and userID = {}", eventId, userId);
        return requestService.getAllByOwner(eventId, userId);
    }
}

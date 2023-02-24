package ru.practicum.explore.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.comment.dto.CommentDto;
import ru.practicum.explore.comment.dto.CommentDtoNew;
import ru.practicum.explore.comment.dto.CommentDtoUpdate;
import ru.practicum.explore.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;


@RestController
@RequestMapping(path = "/users/{userId}/comments/{eventId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@RequestBody @Valid CommentDtoNew commentDtoNew, @PathVariable @Positive Long userId,
                             @PathVariable @Positive Long eventId) {
        log.info("Creating comment");
        return commentService.create(commentDtoNew, userId, eventId);
    }

    @PatchMapping
    public CommentDto update(@RequestBody @Valid CommentDtoUpdate commentDtoUpdate, @PathVariable @Positive Long userId,
                             @PathVariable @Positive Long eventId) {
        log.info("Updating comment");
        return commentService.update(commentDtoUpdate, userId, eventId);
    }
}

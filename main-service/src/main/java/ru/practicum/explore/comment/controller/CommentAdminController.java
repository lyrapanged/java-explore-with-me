package ru.practicum.explore.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.comment.dto.CommentDto;
import ru.practicum.explore.comment.service.CommentService;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentAdminController {

    private final CommentService commentService;

    @PatchMapping("/{commentId}/publish")
    public CommentDto publish(@PathVariable @Positive Long commentId) {
        log.info("Publishing comment");
        return commentService.publish(commentId);
    }

    @PatchMapping("/{commentId}/reject")
    public CommentDto reject(@PathVariable @Positive Long commentId) {
        log.info("Cancelling comment");
        return commentService.reject(commentId);
    }

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByAdmin(@PathVariable @Positive Long commentId) {
        log.info("Deleting comment");
        commentService.deleteByAdmin(commentId);
    }
}

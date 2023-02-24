package ru.practicum.explore.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.comment.model.Comment.Status;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentDto {

    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
    private Status status;
}

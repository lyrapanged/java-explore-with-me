package ru.practicum.explore.comment.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.explore.comment.dto.CommentDto;
import ru.practicum.explore.comment.dto.CommentDtoNew;
import ru.practicum.explore.comment.dto.CommentDtoUpdate;

import java.util.List;

public interface CommentService {

    CommentDto create(CommentDtoNew commentDtoNew, Long userId, Long eventId);

    CommentDto update(CommentDtoUpdate commentDtoUpdate, Long userId, Long eventId);

    CommentDto publish(Long commentId);

    CommentDto reject(Long commentId);

    List<CommentDto> getAll(Long eventId, Pageable pageable);

    void deleteByAdmin(Long commentId);

}

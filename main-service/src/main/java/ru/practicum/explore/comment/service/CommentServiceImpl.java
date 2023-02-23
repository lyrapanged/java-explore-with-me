package ru.practicum.explore.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.comment.dto.CommentDto;
import ru.practicum.explore.comment.dto.CommentDtoNew;
import ru.practicum.explore.comment.dto.CommentDtoUpdate;
import ru.practicum.explore.comment.mapper.CommentMapper;
import ru.practicum.explore.comment.model.Comment;
import ru.practicum.explore.comment.repository.CommentRepository;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.explore.exception.NotFoundException;
import ru.practicum.explore.user.model.User;
import ru.practicum.explore.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.explore.comment.model.Comment.Status;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public CommentDto create(CommentDtoNew commentDtoNew, Long userId, Long eventId) {
        User author = getUserOrThrow(userId);
        getEventOrThrow(eventId);
        Comment comment = commentMapper.commentDtoNewToComment(commentDtoNew, author, eventId);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto update(CommentDtoUpdate commentDtoUpdate, Long userId, Long eventId) {
        getUserOrThrow(userId);
        getEventOrThrow(eventId);
        Comment comment = getCommentOrThrow(commentDtoUpdate.getId());
        comment.setText(commentDtoUpdate.getText());
        comment.setStatus(Status.PENDING);
        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    public List<CommentDto> getAll(Long eventId, Pageable pageable) {
        getEventOrThrow(eventId);
        return commentRepository.findAllByEventId(eventId, pageable).stream()
                .map(commentMapper::commentToCommentDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto publish(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        comment.setStatus(Status.PUBLISHED);
        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto reject(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        comment.setStatus(Status.CANCELED);
        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long commentId) {
        getCommentOrThrow(commentId);
        commentRepository.deleteById(commentId);
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event does not exist"));
    }

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment does not exist"));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User does not exist"));
    }
}

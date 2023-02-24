package ru.practicum.explore.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.explore.comment.dto.CommentDto;
import ru.practicum.explore.comment.dto.CommentDtoNew;
import ru.practicum.explore.comment.model.Comment;
import ru.practicum.explore.user.mapper.UserMapper;
import ru.practicum.explore.user.model.User;

@Mapper(componentModel = "spring",
        uses = {
                UserMapper.class,
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    Comment commentDtoNewToComment(CommentDtoNew commentDtoNew, User author, Long eventId);

    @Mapping(target = "authorName", source = "author.name")
    CommentDto commentToCommentDto(Comment comment);

}

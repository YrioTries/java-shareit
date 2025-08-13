package ru.practicum.shareit.entity.comment.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapperAn {
    @Mapping(source = "author.name", target = "authorName")
    CommentDto toCommentDto(Comment comment);

    @Mapping(source = "authorName", target = "author.name")
    Comment toComment(CommentDto commentDto);
}

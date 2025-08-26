package ru.practicum.shareit.server.entity.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.entity.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);
}

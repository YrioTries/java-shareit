package ru.practicum.shareit.entity.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.entity.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);
}

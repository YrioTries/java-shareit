package ru.practicum.shareit.server.entity.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.server.entity.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.author.id = :userId")
    void deleteByAuthorId(@Param("userId") Long userId);
}

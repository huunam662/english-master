package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    boolean existsByCommentParent(Comment comment);

    Optional<Comment> findByCommentId(UUID commentId);
    List<Comment> findAllByCommentParent(Comment comment);
}

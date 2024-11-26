package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    boolean existsByCommentParent(Comment comment);

    Optional<Comment> findByCommentId(UUID commentId);
    List<Comment> findAllByCommentParent(Comment comment);
}

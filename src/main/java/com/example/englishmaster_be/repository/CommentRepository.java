package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    boolean existsByCommentParent(CommentEntity comment);

    Optional<CommentEntity> findByCommentId(UUID commentId);

    List<CommentEntity> findAllByCommentParent(CommentEntity comment);
}

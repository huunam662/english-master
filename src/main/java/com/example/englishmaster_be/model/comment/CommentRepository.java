package com.example.englishmaster_be.model.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    boolean existsByCommentParent(CommentEntity comment);

    @Query("""
        SELECT c FROM CommentEntity c
        LEFT JOIN FETCH c.userComment
        WHERE c.commentId = :commentId
    """)
    Optional<CommentEntity> findByCommentId(@Param("commentId") UUID commentId);

    List<CommentEntity> findAllByCommentParent(CommentEntity comment);

    @Query(value = """
        SELECT DISTINCT c FROM CommentEntity c
        LEFT JOIN FETCH c.userComment author
        LEFT JOIN FETCH c.usersVotes uv
        LEFT JOIN FETCH c.commentChildren cmc
        WHERE c.newsId = :newsId
        AND c.isCommentParent = TRUE
        ORDER BY c.createAt DESC
    """)
    Page<CommentEntity> findAllCommentsByNewsId(
            @Param("newsId") UUID newsId,
            Pageable pageable
    );

    @Query(value = """
        SELECT DISTINCT c FROM CommentEntity c
        LEFT JOIN FETCH c.userComment author
        LEFT JOIN FETCH c.toOwnerComment tonc
        LEFT JOIN FETCH c.usersVotes uv
        WHERE c.commentParentId = :commentId
        AND c.isCommentParent = FALSE
        ORDER BY c.createAt DESC
    """)
    Page<CommentEntity> findAllCommentsChildByCommentParentId(
            @Param("commentParentId") UUID commentParentId,
            Pageable pageable
    );

    @Query(value = """
        SELECT DISTINCT c FROM CommentEntity c
        INNER JOIN FETCH c.userComment author
        LEFT JOIN FETCH c.toOwnerComment tonc
        LEFT JOIN FETCH c.usersVotes uv
        LEFT JOIN FETCH c.commentChildren cmc
        WHERE c.commentId = :commentId
    """)
    Optional<CommentEntity> findCommentInfoByCommentId(@Param("commentId") UUID commentId);


    @Query("""
        SELECT c FROM CommentEntity c
        WHERE c.commentParentId = :commentParentId
        AND c.isCommentParent = FALSE
    """)
    Page<CommentEntity> findAllCommentChildByParentId(@Param("commentParentId") UUID commentParentId, Pageable pageable);
}

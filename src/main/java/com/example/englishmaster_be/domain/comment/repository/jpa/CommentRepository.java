package com.example.englishmaster_be.domain.comment.repository.jpa;

import com.example.englishmaster_be.domain.comment.dto.projection.ICountReplyCommentProjection;
import com.example.englishmaster_be.domain.comment.dto.projection.IVotesCommentProjection;
import com.example.englishmaster_be.domain.comment.model.CommentEntity;
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
        SELECT c FROM CommentEntity c
        LEFT JOIN FETCH c.userComment author
        WHERE c.newsId = :newsId
        AND c.commentParent IS NULL
        ORDER BY c.createAt DESC
    """)
    Page<CommentEntity> findAllCommentsByNewsId(
            @Param("newsId") UUID newsId,
            Pageable pageable
    );

    @Query(value = """
        SELECT c FROM CommentEntity c
        LEFT JOIN FETCH c.userComment author
        WHERE c.commentParentId = :commentParentId
        ORDER BY c.createAt DESC
    """)
    Page<CommentEntity> findAllCommentsChildByCommentParentId(
            @Param("commentParentId") UUID commentParentId,
            Pageable pageable
    );

    @Query(value = """
        SELECT c FROM CommentEntity c
        INNER JOIN FETCH c.userComment author
        LEFT JOIN FETCH c.toOwnerComment tonc
        WHERE c.commentId = :commentId
    """)
    Optional<CommentEntity> findCommentInfoByCommentId(@Param("commentId") UUID commentId);


    @Query("""
        SELECT c FROM CommentEntity c
        WHERE c.commentParentId = :commentParentId
    """)
    Page<CommentEntity> findAllCommentChildByParentId(@Param("commentParentId") UUID commentParentId, Pageable pageable);

    @Query(value = """
        SELECT cv.comment_id as commentId, COALESCE(COUNT(cv.user_id), 0) as countVotes
        FROM comments_votes cv
        WHERE cv.comment_id IN :commentIds
        GROUP BY cv.comment_id
    """, nativeQuery = true)
    List<IVotesCommentProjection> countVotesCommentIn(
            @Param("commentIds") List<UUID> commentIds
    );

    @Query(value = """
        SELECT cmc.comment_parent as commentId, COALESCE(COUNT(cmc.id), 0) as countReplies
        FROM comment cmc
        WHERE cmc.comment_parent IN :commentIds
        GROUP BY cmc.comment_parent
    """, nativeQuery = true)
    List<ICountReplyCommentProjection> countReplyCommentIn(
            @Param("commentIds") List<UUID> commentIds
    );
}

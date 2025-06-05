package com.example.englishmaster_be.model.comment;

import com.example.englishmaster_be.model.user.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentJdbcRepository {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional
    public void insertComment(CommentEntity comment){

        if(comment == null) return;

        String sql = """
                    INSERT INTO comment(
                        id, content, create_at, update_at, user_id, 
                        news_id, is_comment_parent, comment_parent, to_owner_comment
                    ) VALUES(:id, :content, now(), now(), :userId, :newsId, :isCommentParent, :commentParent, :toOwnerComment)
                    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", comment.getCommentId())
                .addValue("content", comment.getContent())
                .addValue("isCommentParent", comment.getIsCommentParent())
                .addValue("commentParent", comment.getCommentParent() != null ? comment.getCommentParent().getCommentId() : null)
                .addValue("newsId", comment.getNews() != null ? comment.getNews().getNewsId() : null)
                .addValue("userId", comment.getUserComment() != null ? comment.getUserComment().getUserId() : null)
                .addValue("toOwnerComment", comment.getToOwnerComment() != null ? comment.getToOwnerComment().getUserId() : null);

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Transactional
    public void updateComment(CommentEntity comment){

        if(comment == null) return;

        String sql = """
                    UPDATE comment
                    SET content = :commentContent,
                        update_at = now()
                    WHERE id = :commentId
                    """;

        namedParameterJdbcTemplate.update(
                sql, new MapSqlParameterSource()
                        .addValue("commentContent", comment.getContent())
                        .addValue("commentId", comment.getCommentId())
        );
    }

    @Transactional
    public void deleteCommentsVotes(CommentEntity comment, UserEntity userVote){

        if(comment == null || userVote == null) return;

        String sql = """
                        DELETE FROM comments_votes
                        WHERE user_id = :userId
                        AND comment_id = :commentId
                    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userVote.getUserId())
                .addValue("commentId", comment.getCommentId());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Transactional
    public void insertCommentsVotes(CommentEntity comment, UserEntity userVote){

        if(comment == null || userVote == null) return;

        String sql = """
                        INSERT INTO comments_votes(
                            user_id, comment_id
                        ) VALUES(:userId, :commentId)
                    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userVote.getUserId())
                .addValue("commentId", comment.getCommentId());

        namedParameterJdbcTemplate.update(sql, params);
    }
}

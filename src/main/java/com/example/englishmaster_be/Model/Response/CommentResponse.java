package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {

    UUID commentId;

    UUID userId;

    UUID topicId;

    UUID postId;

    String userName;

    String avatar;

    String contentComment;

    String createAt;

    String updateAt;

    boolean isCommentParent;

    public CommentResponse(Comment comment, boolean isCommentParent) {

        if(Objects.isNull(comment)) return;

        this.commentId = comment.getCommentId();

        if(Objects.nonNull(comment.getUserComment())){
            this.userName = comment.getUserComment().getName();
            this.userId = comment.getUserComment().getUserId();
            this.avatar = comment.getUserComment().getAvatar();
        }

        this.contentComment = comment.getContent();

        if (Objects.nonNull(comment.getTopic()))
            this.topicId = comment.getTopic().getTopicId();

        if (Objects.nonNull(comment.getPost()))
            this.topicId = comment.getPost().getPostId();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        if(Objects.nonNull(comment.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(comment.getCreateAt()));
        if(Objects.nonNull(comment.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(comment.getUpdateAt()));

        this.isCommentParent = isCommentParent;
    }

}

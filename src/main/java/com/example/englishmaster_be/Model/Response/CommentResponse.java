package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Model.*;
import lombok.Getter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class CommentResponse {
    @Getter
    private UUID commentId;
    @Getter
    private UUID userId;
    @Getter
    private String userName;

    @Getter
    private String avatar;

    @Getter
    private String contentComment;
    @Getter
    private UUID topicId;

    private boolean isCommentParent;

    @Getter
    private String createAt;
    @Getter
    private String updateAt;

    public CommentResponse(Comment comment, boolean isCommentParent){
        this.commentId = comment.getCommentId();
        this.userName = comment.getUserComment().getName();
        this.userId = comment.getUserComment().getUserId();

        if(comment.getUserComment().getAvatar() == null){
            this.avatar = null;
        }else {
            String link = GetExtension.linkName(comment.getUserComment().getAvatar());
            this.avatar = link + comment.getUserComment().getAvatar();
        }
        this.contentComment = comment.getContent();
        this.topicId = comment.getTopic().getTopicId();
        this.isCommentParent = isCommentParent;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(comment.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(comment.getUpdateAt()));
    }

    public void setCommentId(UUID commentId) {
        this.commentId = commentId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setContentComment(String contentComment) {
        this.contentComment = contentComment;
    }

    public void setTopicId(UUID topicId) {
        this.topicId = topicId;
    }

    public boolean isCommentParent() {
        return isCommentParent;
    }

    public void setCommentParent(boolean commentParent) {
        isCommentParent = commentParent;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}

package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Model.*;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class CommentResponse {

    private UUID commentId;


    private UUID userId;


    private String userName;


    private String avatar;


    private String contentComment;


    private UUID topicId;

    private UUID postId;

    private boolean isCommentParent;


    private String createAt;

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
        if(comment.getTopic() != null){
            this.topicId = comment.getTopic().getTopicId();
        }
        if(comment.getPost() != null){
            this.topicId = comment.getPost().getPostId();
        }
        this.isCommentParent = isCommentParent;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(comment.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(comment.getUpdateAt()));
    }

    public UUID getCommentId() {
        return commentId;
    }

    public void setCommentId(UUID commentId) {
        this.commentId = commentId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContentComment() {
        return contentComment;
    }

    public void setContentComment(String contentComment) {
        this.contentComment = contentComment;
    }

    public UUID getTopicId() {
        return topicId;
    }

    public void setTopicId(UUID topicId) {
        this.topicId = topicId;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public boolean isCommentParent() {
        return isCommentParent;
    }

    public void setCommentParent(boolean commentParent) {
        isCommentParent = commentParent;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}

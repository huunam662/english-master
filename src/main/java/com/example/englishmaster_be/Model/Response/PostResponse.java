package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Model.Post;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class PostResponse {
    private UUID postId;

    private String userName;
    private UUID userId;
    private String userAvatar;
    private String content;
    private int numberComment;
    private String createAt;
    private String updateAt;

    public PostResponse(Post post){
        this.postId = post.getPostId();
        this.userName = post.getUserPost().getName();
        this.userId = post.getUserPost().getUserId();
        this.content = post.getContent();

        if(post.getComments() != null){
            this.numberComment = post.getComments().stream().toList().size();
        }else this.numberComment = 0;

        if(post.getUserPost().getAvatar() == null){
            this.userAvatar = null;
        }else {
            String link = GetExtension.linkName(post.getUserPost().getAvatar());
            this.userAvatar = link + post.getUserPost().getAvatar();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(post.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(post.getUpdateAt()));
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNumberComment() {
        return numberComment;
    }

    public void setNumberComment(int numberComment) {
        this.numberComment = numberComment;
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

package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.helper.GetExtension;
import com.example.englishmaster_be.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Getter
@Setter
public class PostResponse {
    private UUID postId;
    private String userName;
    private UUID userId;
    private String userAvatar;
    private String content;
    private int numberComment;
    private String createAt;
    private String updateAt;

    public PostResponse(Post post) {
        this.postId = post.getPostId();
        this.userName = post.getUserPost().getName();
        this.userId = post.getUserPost().getUserId();
        this.content = post.getContent();

        if (post.getComments() != null) {
            this.numberComment = post.getComments().stream().toList().size();
        } else this.numberComment = 0;

        if (post.getUserPost().getAvatar() == null) {
            this.userAvatar = null;
        } else {
            this.userAvatar = post.getUserPost().getAvatar();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(post.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(post.getUpdateAt()));
    }

}

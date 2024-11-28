package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.Post;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {

    UUID postId;

    UUID userId;

    String userName;

    String userAvatar;

    String content;

    String createAt;

    String updateAt;

    int numberComment;

    public PostResponse(Post post) {

        if(Objects.isNull(post)) return;

        this.postId = post.getPostId();
        this.userName = post.getUserPost().getName();
        this.userId = post.getUserPost().getUserId();
        this.content = post.getContent();

        this.numberComment = Objects.nonNull(post.getComments()) ? post.getComments().size() : 0;
        this.userAvatar = Objects.nonNull(post.getUserPost().getAvatar()) ? post.getUserPost().getAvatar() : null;

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        if(Objects.nonNull(post.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(post.getCreateAt()));
        if(Objects.nonNull(post.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(post.getUpdateAt()));
    }

}

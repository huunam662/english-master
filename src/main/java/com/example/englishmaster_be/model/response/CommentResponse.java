package com.example.englishmaster_be.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {

    UUID commentId;

    UUID topicId;

    UUID postId;

    String contentComment;

    String tagAuthorParent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime updateAt;

    UserCommentResponse authorComment;

    Boolean hasCommentParent;

    Boolean hasCommentChildren;

}

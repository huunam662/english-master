package com.example.englishmaster_be.domain.news.comment.dto.res;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CommentChildRes {

    private UUID commentId;

    private UUID commentParentId;

    private Boolean isCommentParent;

    private Integer numberOfVotes;

    private String commentContent;

    
    private LocalDateTime timeOfComment;

    private String commentToOwnerTag;

    private AuthorCommentRes authorComment;
}

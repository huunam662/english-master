package com.example.englishmaster_be.domain.news.comment.dto.res;


import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CommentNewsRes {

    private UUID commentId;

    private UUID newsId;

    private Boolean isCommentParent;

    private Integer numberOfVotes;

    private Integer numberOfCommentsChild;

    private String commentContent;

    
    private LocalDateTime timeOfComment;

    private AuthorCommentRes authorComment;

}

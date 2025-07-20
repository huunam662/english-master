package com.example.englishmaster_be.domain.news.comment.dto.res;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CommentKeyRes {

    private UUID commentId;

    public CommentKeyRes(UUID commentId) {
        this.commentId = commentId;
    }
}

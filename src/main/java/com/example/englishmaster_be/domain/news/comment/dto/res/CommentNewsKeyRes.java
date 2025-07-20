package com.example.englishmaster_be.domain.news.comment.dto.res;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CommentNewsKeyRes {

    private UUID newsId;

    private UUID commentId;

}

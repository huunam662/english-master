package com.example.englishmaster_be.domain.comment.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentAuthorResponse {

    UUID userId;

    String name;

    String email;

    String avatar;

}

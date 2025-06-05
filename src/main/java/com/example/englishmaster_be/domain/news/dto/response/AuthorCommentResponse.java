package com.example.englishmaster_be.domain.news.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorCommentResponse {

    UUID userId;

    String name;

    String email;

    String avatar;

}

package com.example.englishmaster_be.domain.news.comment.dto.res;


import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
public class AuthorCommentRes {

    private UUID userId;

    private String name;

    private String email;

    private String avatar;

}

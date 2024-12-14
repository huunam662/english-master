package com.example.englishmaster_be.Model.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCommentResponse {

    UUID userId;

    String name;

    String email;

    String avatar;

}

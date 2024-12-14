package com.example.englishmaster_be.Model.Request.Post;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequest {

    @Hidden
    UUID postId;

    String content;

}

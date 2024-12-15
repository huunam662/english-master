package com.example.englishmaster_be.domain.news.dto.request;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsRequest {

    @Hidden
    UUID newsId;

    String title;

    String content;

    Boolean enable;

    MultipartFile image;

}

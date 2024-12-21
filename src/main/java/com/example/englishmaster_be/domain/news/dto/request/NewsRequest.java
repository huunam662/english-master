package com.example.englishmaster_be.domain.news.dto.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Tiêu đề")
    String title;

    @Schema(description = "Nội dung")
    String content;

    @Schema(description = "Kích hoạt")
    Boolean enable;

    String image;

}

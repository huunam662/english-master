package com.example.englishmaster_be.domain.news.news.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
public class NewsRes {

    private UUID newsId;
    private String title;
    private String content;
    private String image;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean enable;

}

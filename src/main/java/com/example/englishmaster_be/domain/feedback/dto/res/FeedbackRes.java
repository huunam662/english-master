package com.example.englishmaster_be.domain.feedback.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
public class FeedbackRes {

    private UUID feedbackId;
    private String name;
    private String description;
    private String avatar;
    private String content;
    private Integer star;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean enable;

}

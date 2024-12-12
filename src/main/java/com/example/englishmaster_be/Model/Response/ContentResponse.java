package com.example.englishmaster_be.Model.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentResponse {

    UUID contentId;

    UUID topicId;

    UUID questionId;

    String contentType;

    String contentData;

    String code;

    UserBasicResponse userCreate;

    UserBasicResponse userUpdate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime updatedAt;

}

package com.example.englishmaster_be.Model.Request.Feedback;

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
public class FeedbackRequest {

    @Hidden
    UUID feedbackId;

    String name;

    String description;

    String content;

    MultipartFile avatar;

}

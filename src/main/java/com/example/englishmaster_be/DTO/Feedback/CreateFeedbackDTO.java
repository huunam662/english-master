package com.example.englishmaster_be.DTO.Feedback;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFeedbackDTO {

    String name;

    String description;

    String content;

    MultipartFile avatar;

}

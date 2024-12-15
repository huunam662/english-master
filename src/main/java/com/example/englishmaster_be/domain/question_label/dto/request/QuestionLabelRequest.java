package com.example.englishmaster_be.domain.question_label.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionLabelRequest {
    String title;
    String label;
    UUID questionId;
}

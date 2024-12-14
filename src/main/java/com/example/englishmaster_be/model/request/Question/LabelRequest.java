package com.example.englishmaster_be.model.request.Question;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabelRequest {
    String title;
    String label;
    UUID questionId;
}

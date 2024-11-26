package com.example.englishmaster_be.dto.mockTest;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateResultMockTestDTO {
    UUID resultMockTestId;
    UUID partId;
    int correctAnswer;
    int score;
    UUID mockTestId;
}

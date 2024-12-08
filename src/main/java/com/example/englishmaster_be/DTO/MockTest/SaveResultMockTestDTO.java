package com.example.englishmaster_be.DTO.MockTest;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveResultMockTestDTO {

    UUID resultMockTestId;

    UUID partId;

    UUID mockTestId;

    int correctAnswer;

    int score;

}

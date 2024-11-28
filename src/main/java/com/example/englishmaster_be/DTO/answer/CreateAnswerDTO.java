package com.example.englishmaster_be.DTO.answer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAnswerDTO {

    UUID questionId;

    String contentAnswer;

    String explainDetails;

    boolean correctAnswer;

}

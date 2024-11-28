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
public class CreateListAnswerDTO {

    UUID idAnswer;

    String contentAnswer;

    boolean correctAnswer;

}

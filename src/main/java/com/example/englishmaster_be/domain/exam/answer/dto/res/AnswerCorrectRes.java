package com.example.englishmaster_be.domain.exam.answer.dto.res;

import lombok.*;

@Data
@NoArgsConstructor
public class AnswerCorrectRes {

    private Boolean correctAnswer;
    private Integer scoreAnswer;

}

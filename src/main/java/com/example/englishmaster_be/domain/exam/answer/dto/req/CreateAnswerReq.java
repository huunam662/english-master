package com.example.englishmaster_be.domain.exam.answer.dto.req;

import lombok.*;

@Data
@NoArgsConstructor
public class CreateAnswerReq {

    private String answerContent;
    private String explainDetails;
    private Boolean correctAnswer;

}

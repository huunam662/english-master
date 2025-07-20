package com.example.englishmaster_be.domain.exam.answer.dto.req;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
public class EditAnswerReq {

    private UUID answerId;
    private String answerContent;
    private String explainDetails;
    private Boolean correctAnswer;

}

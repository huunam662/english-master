package com.example.englishmaster_be.domain.exam.answer.dto.req;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
public class AnswerReq {

    @Hidden
    private UUID answerId;
    private UUID questionId;
    private String answerContent;
    private String explainDetails;
    private Boolean correctAnswer;

}

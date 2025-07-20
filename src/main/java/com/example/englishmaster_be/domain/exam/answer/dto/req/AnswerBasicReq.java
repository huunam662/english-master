package com.example.englishmaster_be.domain.exam.answer.dto.req;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
public class AnswerBasicReq {

    private UUID answerId;
    private String answerContent;
    private Boolean correctAnswer;

}

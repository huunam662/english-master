package com.example.englishmaster_be.domain.exam.answer.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
public class AnswerRes {

    private UUID questionId;
	private UUID answerId;
	private String answerContent;
    private String explainDetails;
    private Boolean correctAnswer;
	private LocalDateTime createAt;
	private LocalDateTime updateAt;

}

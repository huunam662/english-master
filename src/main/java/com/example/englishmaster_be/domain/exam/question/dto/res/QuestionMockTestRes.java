package com.example.englishmaster_be.domain.exam.question.dto.res;

import com.example.englishmaster_be.domain.exam.answer.dto.res.AnswerRes;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
public class QuestionMockTestRes {

    private UUID questionId;

    private UUID answerCorrect;

    private UUID answerChoice;

    private UUID partId;

    private String questionContent;

    
    private LocalDateTime createAt;

    
    private LocalDateTime updateAt;

    private Integer questionScore;

    private List<QuestionMockTestRes> questionGroup;

    private List<AnswerRes> listAnswer;

}

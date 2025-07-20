package com.example.englishmaster_be.domain.exam.question.dto.req;

import com.example.englishmaster_be.domain.exam.answer.dto.req.CreateAnswerReq;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateQuestionChildReq {

    private String questionTitle;

    private String questionContent;

    private String contentAudio;

    private String contentImage;

    private Integer numberChoice;

    private Integer questionScore;

    private List<CreateAnswerReq> answers;
}

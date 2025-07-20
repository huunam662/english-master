package com.example.englishmaster_be.domain.exam.question.dto.req;

import com.example.englishmaster_be.domain.exam.answer.dto.req.EditAnswerReq;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class EditQuestionChildReq {

    private UUID questionChildId;

    private String questionTitle;

    private String questionContent;

    private String contentAudio;

    private String contentImage;

    private Integer numberChoice;

    private Integer questionScore;

    private List<EditAnswerReq> answers;
}

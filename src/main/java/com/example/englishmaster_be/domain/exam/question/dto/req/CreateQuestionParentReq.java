package com.example.englishmaster_be.domain.exam.question.dto.req;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateQuestionParentReq {

    private String questionTitle;

    private String questionContent;

    private String contentAudio;

    private String contentImage;

    private List<CreateQuestionChildReq> questionChilds;

}

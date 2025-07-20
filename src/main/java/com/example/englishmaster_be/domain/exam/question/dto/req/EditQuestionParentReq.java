package com.example.englishmaster_be.domain.exam.question.dto.req;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class EditQuestionParentReq {

    private UUID questionParentId;

    private String questionTitle;

    private String questionContent;

    private String contentAudio;

    private String contentImage;

    private List<EditQuestionChildReq> questionChilds;

}

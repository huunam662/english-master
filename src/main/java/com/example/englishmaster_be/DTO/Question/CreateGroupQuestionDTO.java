package com.example.englishmaster_be.DTO.Question;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CreateGroupQuestionDTO {
    private String questionContent;

    private int questionScore;

    private int questionNumberical;

    private UUID questionGroupId;
}

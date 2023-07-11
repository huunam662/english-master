package com.example.englishmaster_be.DTO.Question;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class CreateQuestionDTO {

    private String questionContent;

    private int questionScore;

    private UUID partId;

}

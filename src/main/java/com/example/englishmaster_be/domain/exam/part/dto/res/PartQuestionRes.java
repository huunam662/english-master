package com.example.englishmaster_be.domain.exam.part.dto.res;

import com.example.englishmaster_be.domain.exam.question.dto.res.QuestionRes;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PartQuestionRes {

    private UUID partId;

    private String partName;

    private List<QuestionRes> questions;
}

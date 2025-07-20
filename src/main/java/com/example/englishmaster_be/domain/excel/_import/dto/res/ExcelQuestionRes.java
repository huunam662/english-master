package com.example.englishmaster_be.domain.excel._import.dto.res;

import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.domain.exam.answer.dto.res.AnswerRes;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ExcelQuestionRes {

    private UUID questionId;

    private UUID partId;

    private UUID topicId;

    private String questionContent;

    private String questionResult;

    private String questionExplainEn;

    private String questionExplainVn;

    private String contentAudio;

    private String contentImage;

    private Integer questionScore;

    private Boolean isQuestionParent;

    private QuestionType questionType;

    private List<AnswerRes> answers;

    private List<ExcelQuestionRes> questionsChildren;
}

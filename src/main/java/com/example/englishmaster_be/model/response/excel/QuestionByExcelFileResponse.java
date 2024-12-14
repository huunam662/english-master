package com.example.englishmaster_be.model.response.excel;

import com.example.englishmaster_be.model.request.Answer.AnswerBasicRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionByExcelFileResponse {

    UUID questionId;

    UUID partId;

    String questionContent;

    String contentImage;

    String contentAudio;

    String questionExplainEn;

    String questionExplainVn;

    Integer questionScore;

    List<AnswerBasicRequest> listAnswer;

    List<QuestionByExcelFileResponse> listQuestionChild;


    @Override
    public String toString() {
        return "CreateQuestionByExcelFileDTO{" +
                "questionId=" + questionId +
                ", questionContent='" + questionContent + '\'' +
                ", questionScore=" + questionScore +
                ", contentImage='" + contentImage + '\'' +
                ", contentAudio='" + contentAudio + '\'' +
                ", partId=" + partId +
                ", listAnswer=" + listAnswer +
                ", listQuestionChild=" + listQuestionChild +
                ", questionExplainEn='" + questionExplainEn + '\'' +
                ", questionExplainVn='" + questionExplainVn + '\'' +
                '}';
    }
}

package com.example.englishmaster_be.Model.Response.excel;

import com.example.englishmaster_be.DTO.Answer.CreateListAnswerDTO;
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
public class CreateQuestionByExcelFileResponse {

    UUID questionId;

    UUID partId;

    String questionContent;

    String contentImage;

    String contentAudio;

    String questionExplainEn;

    String questionExplainVn;

    int questionScore;

    List<CreateListAnswerDTO> listAnswer;

    List<CreateQuestionByExcelFileResponse> listQuestionChild;


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

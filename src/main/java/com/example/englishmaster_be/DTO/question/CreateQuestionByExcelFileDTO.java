package com.example.englishmaster_be.DTO.question;

import com.example.englishmaster_be.DTO.answer.CreateListAnswerDTO;
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
public class CreateQuestionByExcelFileDTO {

    UUID questionId;

    UUID partId;

    String questionContent;

    String contentImage;

    String contentAudio;

    String questionExplainEn;

    String questionExplainVn;

    int questionScore;

    List<CreateListAnswerDTO> listAnswer;

    List<CreateQuestionByExcelFileDTO> listQuestionChild;


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

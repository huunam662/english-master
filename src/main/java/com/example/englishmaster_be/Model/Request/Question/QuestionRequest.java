package com.example.englishmaster_be.Model.Request.Question;

import com.example.englishmaster_be.Model.Request.Answer.AnswerBasicRequest;
import com.example.englishmaster_be.Common.enums.QuestionTypeEnum;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionRequest {

    @Hidden
    UUID questionId;

    UUID partId;

    String questionContent;

    String questionExplainEn;

    String questionExplainVn;

    Integer questionScore;

    Integer numberChoice;

    QuestionTypeEnum questionType;

    MultipartFile contentImage;

    MultipartFile contentAudio;

    List<AnswerBasicRequest> listAnswer;

    List<QuestionRequest> listQuestionChild;

    boolean hasHints;

}

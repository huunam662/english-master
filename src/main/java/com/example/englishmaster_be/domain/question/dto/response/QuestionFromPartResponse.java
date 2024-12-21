package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.common.constant.QuestionTypeEnum;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionFromPartResponse {

    UUID questionId;

    QuestionTypeEnum questionType;

    String content;

    List<AnswerMatchingBasicResponse> options; // lua chon cau hoi matching

    List<String> answers;

    List<String> labels;
}

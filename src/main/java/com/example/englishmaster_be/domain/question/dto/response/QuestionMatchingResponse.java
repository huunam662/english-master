package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.common.constant.QuestionTypeEnum;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.example.englishmaster_be.domain.content.dto.response.ContentBasicResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class QuestionMatchingResponse extends QuestionResponse{

    List<QuestionMatchingResponse> contentLeft;

    List<QuestionMatchingResponse> contentRight;

}

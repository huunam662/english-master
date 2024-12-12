package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Common.enums.QuestionTypeEnum;
import com.example.englishmaster_be.entity.QuestionEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResponse extends QuestionBasicResponse{

    @JsonInclude(JsonInclude.Include.NON_NULL)
    QuestionBasicResponse questionGroupParent;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<QuestionBasicResponse> questionGroupChildren;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<AnswerResponse> listAnswer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ContentResponse> contentList;

}

package com.example.englishmaster_be.domain.question.dto.response;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIncludeProperties(value = {"contentLeft", "contentRight"})
public class QuestionMatchingResponse extends QuestionResponse{

    List<QuestionResponse> contentLeft;

    List<QuestionResponse> contentRight;

}

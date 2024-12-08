package com.example.englishmaster_be.DTO.Question;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveGroupQuestionDTO {

	UUID questionGroupId;

	String questionContent;

	String questionExplainEn;

	String questionExplainVn;

	int questionScore;

	int questionNumberical;

}

package com.example.englishmaster_be.domain.question.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionGroupRequest {

	UUID questionGroupId;

//	String questionContent;
//
//	String questionExplainEn;
//
//	String questionExplainVn;
//
//	Integer questionScore;
//
//	Integer questionNumberical;

}

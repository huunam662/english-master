package com.example.englishmaster_be.DTO.Question;

import com.example.englishmaster_be.DTO.Answer.CreateListAnswerDTO;
import com.example.englishmaster_be.DTO.Type.QuestionType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateQuestionDTO {

	UUID questionId;

    String questionContent;

    int questionScore;

	MultipartFile contentImage;

	MultipartFile contentAudio;

    UUID partId;

	QuestionType questionType;

	List<CreateListAnswerDTO> listAnswer;

	List<CreateQuestionDTO> listQuestionChild;

	String questionExplainEn;

	String questionExplainVn;

	int numberChoice;

}

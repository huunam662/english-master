package com.example.englishmaster_be.DTO.Question;

import com.example.englishmaster_be.DTO.Answer.SaveListAnswerDTO;
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
public class SaveQuestionDTO {

	UUID questionId;

    String questionContent;

    int questionScore;

	MultipartFile contentImage;

	MultipartFile contentAudio;

    UUID partId;

	String questionType;

	List<SaveListAnswerDTO> listAnswer;

	List<SaveQuestionDTO> listQuestionChild;

	String questionExplainEn;

	String questionExplainVn;

}

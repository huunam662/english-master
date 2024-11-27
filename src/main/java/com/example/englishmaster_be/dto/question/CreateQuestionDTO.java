package com.example.englishmaster_be.dto.question;

import com.example.englishmaster_be.dto.answer.CreateListAnswerDTO;
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

    UUID partId;

    String questionContent;

	String questionExplainEn;

	String questionExplainVn;

    int questionScore;

	MultipartFile contentImage;

	MultipartFile contentAudio;

	List<CreateListAnswerDTO> listAnswer;

	List<CreateQuestionDTO> listQuestionChild;

}

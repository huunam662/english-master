package com.example.englishmaster_be.domain.exam.question.dto.res;

import com.example.englishmaster_be.common.constant.QuestionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class QuestionReadingListeningRes {

    private UUID questionId;
    private String questionTitle;
    private String questionContent;
    private Integer questionNumber;
    private Integer questionScore;
    private String contentAudio;
    private String contentImage;
    private String questionResult;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

}

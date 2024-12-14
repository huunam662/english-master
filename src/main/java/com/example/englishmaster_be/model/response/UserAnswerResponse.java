package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.entity.AnswerEntity;
import com.example.englishmaster_be.entity.QuestionEntity;
import com.example.englishmaster_be.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerResponse {

    UUID id;

    String content;

    Integer numberChoice;

    QuestionBasicResponse question;

    UserBasicResponse user;

    List<AnswerResponse> answers;

}

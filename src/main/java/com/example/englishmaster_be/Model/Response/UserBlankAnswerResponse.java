package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.entity.QuestionEntity;
import com.example.englishmaster_be.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserBlankAnswerResponse {

    UUID id;

    String answer;

    Integer position;

    QuestionBasicResponse question;

    UserBasicResponse user;

}

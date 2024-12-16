package com.example.englishmaster_be.model.user_blank_answer;

import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(hidden = true)
@Entity
@Table(name = "user_blank_answers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserBlankAnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String answer;

    Integer position;

    @ManyToOne
    @JoinColumn(name = "question_id")
    QuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;

}

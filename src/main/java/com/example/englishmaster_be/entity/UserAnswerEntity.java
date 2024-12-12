package com.example.englishmaster_be.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(hidden = true)
@Entity
@Table(name = "user_answers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String content;

    @Column(name = "number_choice")
    Integer numberChoice;

    @ManyToOne
    @JoinColumn(name = "question_id")
    QuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;

    @ManyToMany(mappedBy = "userAnswers", fetch = FetchType.LAZY)
    List<AnswerEntity> answers;
}

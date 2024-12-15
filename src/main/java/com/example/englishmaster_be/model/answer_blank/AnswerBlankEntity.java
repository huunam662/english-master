package com.example.englishmaster_be.model.answer_blank;

import com.example.englishmaster_be.model.question.QuestionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


@Getter
@Setter
@Builder
@Entity
@Table(name = "anwser_blanks")
@Schema(hidden = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerBlankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    QuestionEntity question;

    Integer position;

    String answer;

}

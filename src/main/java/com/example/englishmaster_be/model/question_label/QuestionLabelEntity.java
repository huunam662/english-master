package com.example.englishmaster_be.model.question_label;

import com.example.englishmaster_be.model.question.QuestionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_label")
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionLabelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String content;

    String label;

    @ManyToOne
    @JoinColumn(name = "question_id")
    QuestionEntity question;
}

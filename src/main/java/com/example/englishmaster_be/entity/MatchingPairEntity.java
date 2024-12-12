package com.example.englishmaster_be.entity;

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
@Table(name = "matching_pairs")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchingPairEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String content;

    String answer;

    @ManyToOne
    @JoinColumn(name = "question_id")
    QuestionEntity question;
}

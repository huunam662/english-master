package com.example.englishmaster_be.entity;

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
@Table(name = "answer_matchings")
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerMatchingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "content_left",nullable = false)
    String contentLeft;

    @Column(name = "content_right",nullable = false)
    String contentRight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    QuestionEntity question;

}

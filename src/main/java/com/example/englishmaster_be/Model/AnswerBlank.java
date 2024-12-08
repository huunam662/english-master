package com.example.englishmaster_be.Model;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Hidden
@Getter
@Setter
@Builder
@Entity
@Table(name = "anwser_blanks")
@NoArgsConstructor
@AllArgsConstructor
public class AnswerBlank {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private int position;

    private String answer;
}

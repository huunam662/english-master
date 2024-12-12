package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.DTO.Answer.SaveAnswerDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "Answer")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Answer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID answerId;

    @Column(name = "Content")
    String answerContent;

    @Column(name = "explain_details")
    String explainDetails;

    @Column(name = "correct_answer")
    boolean correctAnswer;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    User userCreate;

    @ManyToOne
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    User userUpdate;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    Question question;

    @ManyToMany
    @JoinTable(
            name = "user_answer_answers",
            joinColumns = @JoinColumn(name = "answer_id"),
            inverseJoinColumns = @JoinColumn(name = "user_answer_id")
    )
    private Set<UserAnswer> userAnswers;

    public Answer(SaveAnswerDTO createAnswerDTO) {

        if (Objects.isNull(createAnswerDTO)) return;

        this.answerContent = createAnswerDTO.getContentAnswer();
        this.correctAnswer = createAnswerDTO.isCorrectAnswer();
        this.explainDetails = createAnswerDTO.getExplainDetails();
    }

}

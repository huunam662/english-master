package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.DTO.Answer.CreateAnswerDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "answer")
public class Answer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID answerId;

    @Column(name = "content")
    private String answerContent;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;

    @Column(name = "correct_answer")
    private boolean correctAnswer;

    @Column(name = "explain_details")
    private String explainDetails;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    private User userCreate;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    private User userUpdate;

    public Answer() {
        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }

    public Answer(CreateAnswerDTO createAnswerDTO){
        this.answerContent = createAnswerDTO.getAnswerContent();
        this.correctAnswer = createAnswerDTO.isCorrectAnswer();
        this.explainDetails = createAnswerDTO.getExplainDetails();

        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }
}

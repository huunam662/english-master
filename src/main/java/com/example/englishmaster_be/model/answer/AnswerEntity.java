package com.example.englishmaster_be.model.answer;

import com.example.englishmaster_be.model.mock_test_detail.MockTestDetailEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "Answer")
@Schema(hidden = true)
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID answerId;

    @Column(name = "Content")
    String answerContent;

    @Column(name = "explain_details")
    String explainDetails;

    @Column(name = "correct_answer")
    Boolean correctAnswer;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    UserEntity userCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    UserEntity userUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    QuestionEntity question;

    @OneToMany(mappedBy = "answerChoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<MockTestDetailEntity> mockTestDetails;

    @PrePersist
    void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    @PreRemove
    void onRemove() {

        if(mockTestDetails != null){
            mockTestDetails.forEach(
                    mockTestDetailEntity -> mockTestDetailEntity.setAnswerChoice(null)
            );
        }

    }
}

package com.example.englishmaster_be.domain.exam.answer.model;

import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.model.ReadingListeningSubmissionEntity;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "Answer")
@Schema(hidden = true)
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AnswerEntity {

    @Id
    @Column(name = "id")
    private UUID answerId;

    @Column(name = "Content")
    private String answerContent;

    @Column(name = "explain_details")
    private String explainDetails;

    @Column(name = "correct_answer")
    private Boolean correctAnswer;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    @CreatedDate
    private LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    @LastModifiedDate
    private LocalDateTime updateAt;

    @Column(name = "question_id", insertable = false, updatable = false)
    private UUID questionChildId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    @CreatedBy
    private UserEntity userCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    @LastModifiedBy
    private UserEntity userUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private QuestionEntity question;

    @OneToMany(mappedBy = "answerChoice", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ReadingListeningSubmissionEntity> readingListeningSubmissions;

}

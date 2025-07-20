package com.example.englishmaster_be.domain.mock_test.reading_listening_submission.model;

import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "reading_listening_submissions")
@Getter
@Setter
@NoArgsConstructor
@Schema(hidden = true)
@EntityListeners(AuditingEntityListener.class)
public class ReadingListeningSubmissionEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "answer_content")
    private String answerContent;

    @Column(name = "is_correct_answer")
    private Boolean isCorrectAnswer;

    @Column(name = "score_achieved")
    private Integer scoreAchieved;

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

    @Column(name = "mock_test_id", insertable = false, updatable = false)
    private UUID mockTestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_choice_id", referencedColumnName = "id")
    private AnswerEntity answerChoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mock_test_id", referencedColumnName = "id")
    private MockTestEntity mockTest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    @CreatedBy
    private UserEntity userCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    @LastModifiedBy
    private UserEntity userUpdate;

}

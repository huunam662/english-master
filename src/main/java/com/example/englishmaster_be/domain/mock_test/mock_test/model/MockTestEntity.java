package com.example.englishmaster_be.domain.mock_test.mock_test.model;

import com.example.englishmaster_be.domain.mock_test.evaluator_writing.model.EssaySubmissionEntity;
import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.model.ReadingListeningSubmissionEntity;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingSubmissionEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "mock_test")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MockTestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID mockTestId;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "total_questions_finish")
    private Integer totalQuestionsFinish;

    @Column(name = "total_questions_skip")
    private Integer totalQuestionsSkip;

    @Column(name = "total_answers_correct")
    private Integer totalAnswersCorrect;

    @Column(name = "total_answers_wrong")
    private Integer totalAnswersWrong;

    @Column(name = "answers_correct_percent", columnDefinition = "NUMERIC(?,?)")
    private Float answersCorrectPercent;

    @Column(name = "work_time")
    private LocalTime workTime;

    @Column(name = "finish_time")
    private LocalTime finishTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private TopicEntity topic;

    @OneToMany(mappedBy = "mockTest", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<ReadingListeningSubmissionEntity> readingListeningSubmissions;

    @OneToMany(mappedBy = "mockTest", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<EssaySubmissionEntity> essaySubmissions;

    @OneToMany(mappedBy = "mockTest", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<SpeakingSubmissionEntity> speakingSubmissions;

}

package com.example.englishmaster_be.model.mock_test;

import com.example.englishmaster_be.model.mock_test_result.MockTestResultEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SortComparator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;


@Entity
@Table(name = "mock_test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestEntity {

    @Id
    @Column(name = "id")
    UUID mockTestId;

    @Column(name = "total_score")
    Integer totalScore;

    @Column(name = "total_questions_finish")
    Integer totalQuestionsFinish;

    @Column(name = "total_questions_skip")
    Integer totalQuestionsSkip;

    @Column(name = "total_answers_correct")
    Integer totalAnswersCorrect;

    @Column(name = "total_answers_wrong")
    Integer totalAnswersWrong;

    @Column(name = "answers_correct_percent")
    Float answersCorrectPercent;

    @Column(name = "work_time")
    LocalTime workTime;

    @Column(name = "finish_time")
    LocalTime finishTime;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    TopicEntity topic;

    @OneToMany(mappedBy = "mockTest", fetch = FetchType.LAZY, orphanRemoval = true)
    Set<MockTestResultEntity> mockTestResults;

    @PrePersist
    void onCreate() {

        if(mockTestId == null)
            mockTestId = UUID.randomUUID();

        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}

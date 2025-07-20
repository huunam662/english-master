package com.example.englishmaster_be.domain.mock_test.speaking_submission.model;

import com.example.englishmaster_be.common.constant.speaking_test.LevelSpeakerType;
import com.example.englishmaster_be.common.constant.speaking_test.StatusSpeakingSubmission;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Table(name = "speaking_submissions")
@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SpeakingSubmissionEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "speaking_text")
    private String speakingText;

    @Column(name = "feedback")
    private String feedback;

    @Convert(converter = LevelSpeakerType.Converter.class)
    @Column(name = "level", columnDefinition = "level_speaker_type")
    private LevelSpeakerType levelSpeaker;

    @Column(name = "reached_percent", columnDefinition = "NUMERIC(?,?)")
    private Float reachedPercent;

    @Column(name = "status", columnDefinition = "status_submission_type")
    @Enumerated(EnumType.STRING)
    private StatusSpeakingSubmission status;

    @Column(name = "create_at")
    @CreatedDate
    private LocalDateTime createAt;

    @Column(name = "update_at")
    @LastModifiedDate
    private LocalDateTime updateAt;

    @Column(name = "question_id", insertable = false, updatable = false)
    private UUID questionId;

    @Column(name = "mock_test_id", insertable = false, updatable = false)
    private UUID mockTestId;

    @ManyToOne(fetch = FetchType.LAZY)
    private QuestionEntity question;

    @ManyToOne(fetch = FetchType.LAZY)
    private MockTestEntity mockTest;

    @OneToMany(mappedBy = "speakingSubmission", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<SpeakingErrorEntity> speakingErrors;

}

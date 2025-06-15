package com.example.englishmaster_be.domain.speaking_submission.model;

import com.example.englishmaster_be.common.constant.speaking_test.LevelSpeakerType;
import com.example.englishmaster_be.common.constant.speaking_test.StatusSpeakingSubmission;
import com.example.englishmaster_be.domain.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Table(name = "speaking_submitssions")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingSubmissionEntity {

    @Id
    @Column(name = "id")
    UUID id;

    @Column(name = "audio_url")
    String audioUrl;

    @Column(name = "speaking_text")
    String speakingText;

    @Column(name = "feedback")
    String feedback;

    @Column(name = "level")
    LevelSpeakerType levelSpeaker;

    @Column(name = "reached_percent")
    Float reachedPercent;

    @Column(name = "status")
    StatusSpeakingSubmission status;

    @Column(name = "create_at")
    LocalDateTime createAt;

    @Column(name = "update_at")
    LocalDateTime updateAt;

    @Column(name = "question_id", insertable = false, updatable = false)
    UUID questionId;

    @Column(name = "mock_test_id", insertable = false, updatable = false)
    UUID mockTestId;

    @ManyToOne(fetch = FetchType.LAZY)
    QuestionEntity question;

    @ManyToOne(fetch = FetchType.LAZY)
    MockTestEntity mockTest;

    @OneToMany(mappedBy = "speakingSubmission", fetch = FetchType.LAZY, orphanRemoval = true)
    Set<SpeakingErrorEntity> speakingErrors;

    @PrePersist
    void prePersist(){
        if(this.id == null) this.id = UUID.randomUUID();

        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate(){
        this.updateAt = LocalDateTime.now();
    }

}

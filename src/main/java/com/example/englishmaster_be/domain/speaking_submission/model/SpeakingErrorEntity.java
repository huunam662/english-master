package com.example.englishmaster_be.domain.speaking_submission.model;

import com.example.englishmaster_be.common.constant.speaking_test.SpeakingErrorType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "speaking_errors")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingErrorEntity {

    @Id
    @Column(name = "id")
    UUID id;

    @Column(name = "error_type", columnDefinition = "speaking_error_type")
    @Enumerated(EnumType.STRING)
    SpeakingErrorType speakingErrorType;

    @Column(name = "word")
    String word;

    @Column(name = "word_recommend")
    String wordRecommend;

    @Column(name = "pronunciation")
    String pronunciation;

    @Column(name = "pronunciation_url")
    String pronunciationUrl;

    @Column(name = "feedback")
    String feedback;

    @Column(name = "create_at")
    LocalDateTime createAt;

    @Column(name = "update_at")
    LocalDateTime updateAt;

    @Column(name = "speaking_submission_id", insertable = false, updatable = false)
    UUID speakingSubmissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    SpeakingSubmissionEntity speakingSubmission;

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

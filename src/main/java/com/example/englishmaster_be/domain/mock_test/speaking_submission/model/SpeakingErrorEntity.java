package com.example.englishmaster_be.domain.mock_test.speaking_submission.model;

import com.example.englishmaster_be.common.constant.speaking_test.SpeakingErrorType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "speaking_errors")
@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SpeakingErrorEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "error_type", columnDefinition = "speaking_error_type")
    @Enumerated(EnumType.STRING)
    private SpeakingErrorType speakingErrorType;

    @Column(name = "word")
    private String word;

    @Column(name = "word_recommend")
    private String wordRecommend;

    @Column(name = "pronunciation")
    private String pronunciation;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "create_at")
    @CreatedDate
    private LocalDateTime createAt;

    @Column(name = "update_at")
    @LastModifiedDate
    private LocalDateTime updateAt;

    @Column(name = "speaking_submission_id", insertable = false, updatable = false)
    private UUID speakingSubmissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    private SpeakingSubmissionEntity speakingSubmission;

}

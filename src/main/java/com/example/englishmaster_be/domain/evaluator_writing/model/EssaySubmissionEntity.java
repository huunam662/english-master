package com.example.englishmaster_be.domain.evaluator_writing.model;

import com.example.englishmaster_be.domain.mock_test.model.MockTestEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "essay_submission")
@Schema(hidden = true)
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class EssaySubmissionEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(name = "id")
        UUID essaySubmissionId;

        @Column(name = "user_essay")
        String userEssay;

        @Column(name = "essay_feedback")
        String essayFeedback;

        @Temporal(TemporalType.TIMESTAMP)
        @CreationTimestamp
        @Column(name = "create_at")
        LocalDateTime createAt;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "mock_test_id", referencedColumnName = "id")
        MockTestEntity mockTest;

        @PrePersist
        void onCreate() {
            createAt = LocalDateTime.now();
        }
}

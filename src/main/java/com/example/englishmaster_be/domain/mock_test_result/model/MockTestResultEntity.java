package com.example.englishmaster_be.domain.mock_test_result.model;


import com.example.englishmaster_be.domain.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "mock_test_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestResultEntity {

    @Id
    @Column(name = "id")
    UUID mockTestResultId;

    @Column(name = "total_score")
    Integer totalScoreResult;

    @Column(name = "total_correct")
    Integer totalCorrect;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt;

    @Column(name = "mock_test_id", insertable = false, updatable = false)
    UUID mockTestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    UserEntity userCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    UserEntity userUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    PartEntity part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mock_test_id", referencedColumnName = "id")
    MockTestEntity mockTest;

    @OneToMany(mappedBy = "resultMockTest", fetch = FetchType.LAZY)
    Set<MockTestDetailEntity> mockTestDetails;

    @PrePersist
    void onCreate() {

        if(mockTestResultId == null)
            mockTestResultId = UUID.randomUUID();

        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}

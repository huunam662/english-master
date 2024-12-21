package com.example.englishmaster_be.model.mock_test;

import com.example.englishmaster_be.model.detail_mock_test.DetailMockTestEntity;
import com.example.englishmaster_be.model.result_mock_test.ResultMockTestEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID mockTestId;

    Integer score;

    @JoinColumn(name = "correct_answers")
    Integer correctAnswers;

    Time time;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    TopicEntity topic;

    @ManyToOne
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    UserEntity userCreate;

    @ManyToOne
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    UserEntity userUpdate;

    @OneToMany(mappedBy = "mockTest",cascade = CascadeType.ALL,orphanRemoval = true)
    List<DetailMockTestEntity> detailMockTests;

    @OneToMany(mappedBy = "mockTest",cascade = CascadeType.ALL,orphanRemoval = true)
    List<ResultMockTestEntity> resultMockTests;



    @PrePersist
    void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}

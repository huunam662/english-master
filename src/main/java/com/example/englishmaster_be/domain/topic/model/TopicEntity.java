package com.example.englishmaster_be.domain.topic.model;

import com.example.englishmaster_be.domain.comment.model.CommentEntity;
import com.example.englishmaster_be.domain.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "topics")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID topicId;

    @Column(name = "topic_name")
    String topicName;

    @Column(name = "topic_image")
    String topicImage;

    @Column(name = "topic_description")
    String topicDescription;

    @Column(name = "work_time")
    LocalTime workTime;

    @Column(name = "number_question")
    Integer numberQuestion;

    @Column(name = "enable")
    Boolean enable;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    LocalDateTime startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    LocalDateTime endTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    LocalDateTime updateTime;

    @Column(name = "pack_id", insertable = false, updatable = false)
    UUID packId;

    @Column(name = "topic_type_id", insertable = false, updatable = false)
    UUID topicTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    UserEntity userCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    UserEntity userUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id", referencedColumnName = "id")
    PackEntity pack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_type_id", referencedColumnName = "id")
    TopicTypeEntity topicType;

    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY)
    Set<CommentEntity> comments;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    Set<MockTestEntity> mockTests;

    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY)
    Set<PartEntity> parts;


    @PrePersist
    void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();

        enable = Boolean.TRUE;
    }

    @PreUpdate
    void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}

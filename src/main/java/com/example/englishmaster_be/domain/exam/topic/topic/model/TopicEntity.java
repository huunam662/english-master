package com.example.englishmaster_be.domain.exam.topic.topic.model;

import com.example.englishmaster_be.domain.mock_test.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.exam.pack.pack.model.PackEntity;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.exam.topic.type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
@Schema(hidden = true)
@EntityListeners(AuditingEntityListener.class)
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID topicId;

    @Column(name = "topic_name")
    private String topicName;

    @Column(name = "topic_image")
    private String topicImage;

    @Column(name = "topic_audio")
    private String topicAudio;

    @Column(name = "topic_description")
    private String topicDescription;

    @Column(name = "work_time")
    private LocalTime workTime;

    @Column(name = "number_question")
    private Integer numberQuestion;

    @Column(name = "enable")
    private Boolean enable;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "pack_id", insertable = false, updatable = false)
    private UUID packId;

    @Column(name = "topic_type_id", insertable = false, updatable = false)
    private UUID topicTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    @CreatedBy
    private UserEntity userCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    @LastModifiedBy
    private UserEntity userUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_id", referencedColumnName = "id")
    private PackEntity pack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_type_id", referencedColumnName = "id")
    private TopicTypeEntity topicType;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<MockTestEntity> mockTests;

    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<PartEntity> parts;

}

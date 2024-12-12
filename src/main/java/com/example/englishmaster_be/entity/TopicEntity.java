package com.example.englishmaster_be.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
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

    @Column(name = "topic_type")
    String topicType;

    @Column(name = "work_time")
    String workTime;

    @Column(name = "number_question")
    Integer numberQuestion;

    @Column(name = "enable")
    Boolean enable;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    LocalDateTime startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    UserEntity userCreate;

    @ManyToOne
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    UserEntity userUpdate;

    @ManyToOne
    @JoinColumn(name = "pack_id", referencedColumnName = "id")
    PackEntity pack;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    StatusEntity status;

    @OneToMany(mappedBy = "topic")
    List<CommentEntity> comments;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL,orphanRemoval = true)
    List<MockTestEntity> mockTests;

    @ManyToMany
    @JoinTable(name = "topic_question",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    List<QuestionEntity> questions;

    @ManyToMany
    @JoinTable(name = "topic_part",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id"))
    List<PartEntity> parts;


    public TopicEntity(String topicName,
                       String topicImage,
                       String topicDescription,
                       String topicType,
                       String workTime,
                       LocalDateTime startTime,
                       LocalDateTime endTime
    ) {
        this.topicName = topicName;
        this.topicImage = topicImage;
        this.topicDescription = topicDescription;
        this.topicType = topicType;
        this.workTime = workTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}

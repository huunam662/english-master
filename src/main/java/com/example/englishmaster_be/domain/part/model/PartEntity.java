package com.example.englishmaster_be.domain.part.model;

import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "Part")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartEntity {

    @Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
    UUID partId;

    @Column(name = "part_name")
    String partName;

    @Column(name = "part_description")
    String partDescription;

    @Column(name = "part_type")
    String partType;

    @Column(name = "content_type")
    String contentType;

    @Column(name = "content_data")
    String contentData;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	@Column(name = "update_at")
	LocalDateTime updateAt;

    @Column(name = "topic_id", insertable = false, updatable = false)
    UUID topicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    TopicEntity topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    UserEntity userCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    UserEntity userUpdate;

    @OneToMany(mappedBy = "part", fetch = FetchType.LAZY)
    Set<QuestionEntity> questions;



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

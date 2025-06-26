package com.example.englishmaster_be.domain.topic_type.model;

import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Table(name = "topic_type")
@Entity
@Getter
@Setter
@Schema(hidden = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicTypeEntity {

    @Id
    @Column(name = "id")
    UUID topicTypeId;

    @Column(name = "type_name")
    String topicTypeName;

    @OneToMany(mappedBy = "topicType", fetch = FetchType.LAZY)
    Set<TopicEntity> topics;

    @Column(name = "create_at")
    LocalDateTime createAt;

    @Column(name = "update_at")
    LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    UserEntity userCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    UserEntity userUpdate;

    @PrePersist
    private void prePersist() {
        if(topicTypeId == null) topicTypeId = UUID.randomUUID();
        updateAt = LocalDateTime.now();
        createAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        updateAt = LocalDateTime.now();
    }
}

package com.example.englishmaster_be.domain.exam.part.model;

import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "Part")
@Getter
@Setter
@NoArgsConstructor
@Schema(hidden = true)
@EntityListeners(AuditingEntityListener.class)
public class PartEntity {

    @Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
    private UUID partId;

    @Column(name = "part_name")
    private String partName;

    @Column(name = "part_description")
    private String partDescription;

    @Column(name = "part_type")
    private String partType;

    @Column(name = "content_data")
    private String contentData;

    @Column(name = "content_type")
    private String contentType;

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

    @Column(name = "topic_id", insertable = false, updatable = false)
    private UUID topicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private TopicEntity topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    @CreatedBy
    private UserEntity userCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    @LastModifiedBy
    private UserEntity userUpdate;

    @OneToMany(mappedBy = "part", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<QuestionEntity> questions;

}

package com.example.englishmaster_be.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "Feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID id;

    @Column(name = "name")
    String name;

    @Column(name = "Content")
    String content;

    @Column(name = "description")
    String description;

    @Column(name = "avatar")
    String avatar;

    Boolean enable;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt = LocalDateTime.now();

}

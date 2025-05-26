package com.example.englishmaster_be.model.comment;

import com.example.englishmaster_be.model.news.NewsEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "Comment")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(hidden = true)
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID commentId;

    @Column(name = "Content")
    String content;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    UserEntity userComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    TopicEntity topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", referencedColumnName = "id")
    NewsEntity news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_parent", referencedColumnName = "id")
    CommentEntity commentParent;

    @OneToMany(mappedBy = "commentParent", fetch = FetchType.LAZY)
    Set<CommentEntity> commentChildren;


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

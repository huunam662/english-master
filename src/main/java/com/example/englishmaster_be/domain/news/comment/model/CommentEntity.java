package com.example.englishmaster_be.domain.news.comment.model;

import com.example.englishmaster_be.domain.news.news.model.NewsEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "Comment")
@Getter
@Setter
@Schema(hidden = true)
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID commentId;

    @Column(name = "Content")
    private String content;

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

    @Column(name = "is_comment_parent")
    private Boolean isCommentParent;

    @Column(name = "news_id", insertable = false, updatable = false)
    private UUID newsId;

    @Column(name = "comment_parent", insertable = false, updatable = false)
    private UUID commentParentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_owner_comment", referencedColumnName = "id")
    private UserEntity toOwnerComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private TopicEntity topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", referencedColumnName = "id")
    private NewsEntity news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_parent", referencedColumnName = "id")
    private CommentEntity commentParent;

    @OneToMany(mappedBy = "commentParent", fetch = FetchType.LAZY)
    private Set<CommentEntity> commentChildren;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "comments_votes",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> usersVotes;

}

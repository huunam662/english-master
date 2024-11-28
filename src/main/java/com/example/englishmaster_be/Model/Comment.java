package com.example.englishmaster_be.Model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Comment")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID commentId;

    @Column(name = "Content")
    String content;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User userComment;

    @ManyToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    Topic topic;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    Post post;

    @ManyToOne
    @JoinColumn(name = "comment_parent", referencedColumnName = "id")
    Comment commentParent;

}

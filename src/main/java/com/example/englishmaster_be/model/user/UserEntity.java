package com.example.englishmaster_be.model.user;

import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenEntity;
import com.example.englishmaster_be.model.flash_card.FlashCardEntity;
import com.example.englishmaster_be.model.post.PostEntity;
import com.example.englishmaster_be.model.role.RoleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "users")
@Getter
@Setter
@Schema(hidden = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID userId;

    @Column(unique = true)
    String email;

    String password;

    String name;

    String phone;

    String address;

    String avatar;
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "last_login")
    LocalDateTime lastLogin = LocalDateTime.now();

    @Column(name = "is_enabled")
    Boolean enabled;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "role", referencedColumnName = "id")
    RoleEntity role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ConfirmationTokenEntity> confirmToken;

    @OneToMany(mappedBy = "userComment", cascade = CascadeType.ALL)
    Collection<CommentEntity> comments;

    @OneToMany(mappedBy = "userPost", cascade = CascadeType.ALL)
    List<PostEntity> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<FlashCardEntity> flashCards;

}

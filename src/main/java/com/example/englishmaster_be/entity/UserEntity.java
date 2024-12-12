package com.example.englishmaster_be.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
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

    String email;

    String password;

    String name;

    String phone;

    String address;

    String avatar;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<ConfirmationTokenEntity> confirmToken;

    @OneToMany(mappedBy = "userComment", cascade = CascadeType.ALL)
    Collection<CommentEntity> comments;

    @OneToMany(mappedBy = "userPost", cascade = CascadeType.ALL)
    List<PostEntity> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<FlashCardEntity> flashCards;


}

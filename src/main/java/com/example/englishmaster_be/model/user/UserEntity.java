package com.example.englishmaster_be.model.user;

import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.invalid_token.InvalidTokenEntity;
import com.example.englishmaster_be.model.otp.OtpEntity;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.flash_card.FlashCardEntity;
import com.example.englishmaster_be.model.post.PostEntity;
import com.example.englishmaster_be.model.role.RoleEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
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
public class UserEntity implements UserDetails {

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
    LocalDateTime lastLogin;

    @Column(name = "is_enabled")
    Boolean enabled;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "role", referencedColumnName = "id")
    RoleEntity role;

    @OneToMany(mappedBy = "user")
    List<OtpEntity> OTPs;

    @OneToMany(mappedBy = "user")
    List<SessionActiveEntity> sessionActives;

    @OneToMany(mappedBy = "user")
    List<InvalidTokenEntity> invalidTokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<SessionActiveEntity> confirmTokens;

    @OneToMany(mappedBy = "userComment", cascade = CascadeType.ALL)
    Collection<CommentEntity> comments;

    @OneToMany(mappedBy = "userPost", cascade = CascadeType.ALL)
    List<PostEntity> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<FlashCardEntity> flashCards;


    @PrePersist
    void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(this.getRole());
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.getEnabled();
    }
}

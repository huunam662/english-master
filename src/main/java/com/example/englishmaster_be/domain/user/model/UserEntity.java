package com.example.englishmaster_be.domain.user.model;

import com.example.englishmaster_be.domain.comment.model.CommentEntity;
import com.example.englishmaster_be.domain.auth.model.InvalidTokenEntity;
import com.example.englishmaster_be.domain.news.model.NewsEntity;
import com.example.englishmaster_be.domain.auth.model.OtpEntity;
import com.example.englishmaster_be.domain.auth.model.SessionActiveEntity;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
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
import java.util.*;


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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role", referencedColumnName = "id")
    RoleEntity role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    Set<OtpEntity> OTPs;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    Set<SessionActiveEntity> sessionActives;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    Set<InvalidTokenEntity> invalidTokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<SessionActiveEntity> confirmTokens;

    @OneToMany(mappedBy = "userCreate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<NewsEntity> news;

    @OneToMany(mappedBy = "createBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<FlashCardEntity> flashCards;

    @OneToMany(mappedBy = "userComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<CommentEntity> comments;

    @OneToMany(mappedBy = "toOwnerComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<CommentEntity> commentsToOwner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "comments_votes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    Set<CommentEntity> commentsVotes;

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

    @JsonIgnore
    public String getPassword() {
        return password;
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

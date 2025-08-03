package com.example.englishmaster_be.domain.user.user.model;

import com.example.englishmaster_be.domain.news.comment.model.CommentEntity;
import com.example.englishmaster_be.domain.user.auth.model.InvalidTokenEntity;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.news.news.model.NewsEntity;
import com.example.englishmaster_be.domain.user.auth.model.OtpEntity;
import com.example.englishmaster_be.domain.user.auth.model.SessionActiveEntity;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.*;


@Entity
@Table(name = "users")
@Getter
@Setter
@Schema(hidden = true)
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID userId;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    private String phone;

    private String address;

    private String avatar;

    @Column(name = "user_type")
    private String userType;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_enabled")
    private Boolean enabled;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role", referencedColumnName = "id")
    private RoleEntity role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<OtpEntity> OTPs;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<SessionActiveEntity> sessionActives;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<InvalidTokenEntity> invalidTokens;

    @OneToMany(mappedBy = "userCreate", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Set<NewsEntity> news;

    @OneToMany(mappedBy = "createBy", fetch = FetchType.LAZY)
    private Set<FlashCardEntity> flashCards;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<MockTestEntity> mockTests;

    @OneToMany(mappedBy = "userComment", fetch = FetchType.LAZY)
    private Set<CommentEntity> comments;

    @OneToMany(mappedBy = "toOwnerComment", fetch = FetchType.LAZY)
    private Set<CommentEntity> commentsToOwner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "comments_votes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    private Set<CommentEntity> commentsVotes;

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

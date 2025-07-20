package com.example.englishmaster_be.domain.user.auth.model;

import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Table(name = "invalid_token")
@Getter
@Setter
@NoArgsConstructor
@Schema(hidden = true)
@EntityListeners(AuditingEntityListener.class)
public class InvalidTokenEntity {

    @Id
    @Column(name = "token", columnDefinition = "TEXT")
    private String token;

    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    @Column(name = "create_at")
    @CreatedDate
    private LocalDateTime createAt;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private InvalidTokenType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

}

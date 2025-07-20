package com.example.englishmaster_be.domain.user.auth.model;

import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="session_active")
@Getter
@Setter
@Schema(hidden = true)
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SessionActiveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID sessionId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SessionActiveType type;

    @Column(name = "code")
    private UUID code;

    @Column(name = "token")
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at")
    @CreationTimestamp
    @CreatedDate
    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

}

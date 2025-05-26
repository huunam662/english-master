package com.example.englishmaster_be.model.session_active;

import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.model.user.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="session_active")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(hidden = true)
@AllArgsConstructor
@NoArgsConstructor
public class SessionActiveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID sessionId;

    @Column(name = "Type")
    @Enumerated(EnumType.STRING)
    SessionActiveType type;

    @Column(name = "code", columnDefinition = "UUID")
    UUID code;

    @Column(name = "token", columnDefinition = "TEXT")
    String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at")
    @CreationTimestamp
    LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    UserEntity user;



    @PrePersist
    void onCreate() {
        createAt = LocalDateTime.now();
    }

}

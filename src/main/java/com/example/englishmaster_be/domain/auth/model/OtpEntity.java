package com.example.englishmaster_be.domain.auth.model;

import com.example.englishmaster_be.common.constant.OtpStatus;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "otp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID id;

    @Column(name = "otp")
    String otp;

    @Column(name = "email")
    String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    OtpStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "expiration_time")
    LocalDateTime expirationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    UserEntity user;



    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

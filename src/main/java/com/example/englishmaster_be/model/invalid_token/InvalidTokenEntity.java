package com.example.englishmaster_be.model.invalid_token;

import com.example.englishmaster_be.common.constant.InvalidTokenTypeEnum;
import com.example.englishmaster_be.common.constant.SessionActiveTypeEnum;
import com.example.englishmaster_be.model.user.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;


@Entity
@Table(name = "invalid_token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidTokenEntity {

    @Id
    @Column(name = "token", columnDefinition = "TEXT")
    String token;

    @Column(name = "expire_time")
    LocalDateTime expireTime;

    @Column(name = "create_at")
    LocalDateTime createAt;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    InvalidTokenTypeEnum type;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    UserEntity user;



    @PrePersist
    void onCreate() {
        createAt = LocalDateTime.now();
    }

}

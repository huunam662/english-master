package com.example.englishmaster_be.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "invalid_token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidToken {
    @Id
    @JoinColumn(name = "token")
    String token;

    @JoinColumn(name = "expire_time")
    LocalDateTime expireTime;
}

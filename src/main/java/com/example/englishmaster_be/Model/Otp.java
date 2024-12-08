package com.example.englishmaster_be.Model;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Hidden
@Entity
@Table(name = "otp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Otp {

    @Id
    String otp;

    String email;

    String status;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    LocalDateTime createdAt = LocalDateTime.now();

    LocalDateTime expirationTime;

}

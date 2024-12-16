package com.example.englishmaster_be.model.otp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


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
    String otp;

    String email;

    String status;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    LocalDateTime createdAt = LocalDateTime.now();

    LocalDateTime expirationTime;

}

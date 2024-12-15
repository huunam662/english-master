package com.example.englishmaster_be.model.invalid_token;

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
    @JoinColumn(name = "token")
    String token;

    @JoinColumn(name = "expire_time")
    LocalDateTime expireTime;

}

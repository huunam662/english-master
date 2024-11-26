package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationTokenResponse {
    private UUID userConfirmTokenId;
    private LocalDateTime createAt;
    private String type;
    private String code;
    private User user;
}

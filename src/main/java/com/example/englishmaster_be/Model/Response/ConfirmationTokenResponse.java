package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
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

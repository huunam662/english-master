package com.example.englishmaster_be.dto.confirmationToken;

import com.example.englishmaster_be.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateConfirmationTokenDTO {
    private User user;
}

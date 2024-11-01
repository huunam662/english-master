package com.example.englishmaster_be.DTO.ConfirmationToken;

import com.example.englishmaster_be.Model.User;
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

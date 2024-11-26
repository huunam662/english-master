package com.example.englishmaster_be.service;

import com.example.englishmaster_be.dto.confirmationToken.CreateConfirmationTokenDTO;
import com.example.englishmaster_be.model.response.ConfirmationTokenResponse;

public interface IConfirmationTokenService {
    ConfirmationTokenResponse createConfirmationToken(CreateConfirmationTokenDTO createConfirmationTokenDTO);
}

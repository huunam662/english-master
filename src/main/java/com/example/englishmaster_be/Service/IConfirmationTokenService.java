package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.confirmationToken.CreateConfirmationTokenDTO;
import com.example.englishmaster_be.Model.Response.ConfirmationTokenResponse;

public interface IConfirmationTokenService {
    ConfirmationTokenResponse createConfirmationToken(CreateConfirmationTokenDTO createConfirmationTokenDTO);
}

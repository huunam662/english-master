package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.ConfirmationToken.SaveConfirmationTokenDTO;
import com.example.englishmaster_be.Model.Response.ConfirmationTokenResponse;

public interface IConfirmationTokenService {
    ConfirmationTokenResponse createConfirmationToken(SaveConfirmationTokenDTO createConfirmationTokenDTO);
}

package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Request.ConfirmationToken.ConfirmationTokenRequest;
import com.example.englishmaster_be.Model.Response.ConfirmationTokenResponse;

public interface IConfirmationTokenService {

    ConfirmationTokenResponse createConfirmationToken(ConfirmationTokenRequest confirmationTokenRequest);

}

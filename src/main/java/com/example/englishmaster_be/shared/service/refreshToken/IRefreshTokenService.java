package com.example.englishmaster_be.shared.service.refreshToken;

import com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenEntity;
import com.example.englishmaster_be.model.user.UserEntity;


public interface IRefreshTokenService {

    ConfirmationTokenEntity findByToken(String token);

    void deleteRefreshToken(String token);

    ConfirmationTokenEntity createRefreshToken(String email);

    void verifyExpiration(ConfirmationTokenEntity token);

    void deleteAllTokenExpired(UserEntity user);
}

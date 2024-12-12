package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.entity.ConfirmationTokenEntity;
import com.example.englishmaster_be.entity.UserEntity;


public interface IRefreshTokenService {

    ConfirmationTokenEntity findByToken(String token);

    void deleteRefreshToken(String token);

    ConfirmationTokenEntity createRefreshToken(String email);

    void verifyExpiration(ConfirmationTokenEntity token);

    void deleteAllTokenExpired(UserEntity user);
}

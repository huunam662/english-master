package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.ConfirmationToken;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Model.User;
import jakarta.transaction.Transactional;

import java.util.UUID;

public interface RefreshTokenService {

    ConfirmationToken findByToken(String token);
    ConfirmationToken createRefreshToken(String email);

    ResponseModel verifyExpiration(ResponseModel responseModel, ConfirmationToken token);

    @Transactional
    int deleteByUserId(UUID userId);

}

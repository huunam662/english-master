package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.ConfirmationToken;
import com.example.englishmaster_be.common.response.ResponseModel;
import com.example.englishmaster_be.model.User;
import org.springframework.stereotype.Service;


public interface IRefreshTokenService {

    ConfirmationToken findByToken(String token);

    void deleteRefreshToken(String token);
    ConfirmationToken createRefreshToken(String email);

    ResponseModel verifyExpiration(ResponseModel responseModel, ConfirmationToken token);

    void deleteAllTokenExpired(User user);
}

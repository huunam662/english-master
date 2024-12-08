package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.ConfirmationToken;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Model.User;


public interface IRefreshTokenService {

    ConfirmationToken findByToken(String token);

    void deleteRefreshToken(String token);

    ConfirmationToken createRefreshToken(String email);

    void verifyExpiration(ConfirmationToken token);

    void deleteAllTokenExpired(User user);
}

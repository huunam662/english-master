package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.ConfirmationToken;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Model.User;
import org.springframework.stereotype.Service;


public interface IRefreshTokenService {

    ConfirmationToken findByToken(String token);

    void deleteRefreshToken(String token);
    ConfirmationToken createRefreshToken(String email);

    ResponseModel verifyExpiration(ResponseModel responseModel, ConfirmationToken token);

    void deleteAllTokenExpired(User user);
}

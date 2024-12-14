package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.response.InvalidTokenResponse;

public interface IInvalidTokenService {

    boolean invalidToken(String token);

    InvalidTokenResponse insertInvalidToken(String token);

}

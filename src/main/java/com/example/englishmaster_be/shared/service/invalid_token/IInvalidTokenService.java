package com.example.englishmaster_be.shared.service.invalid_token;

import com.example.englishmaster_be.shared.dto.response.invalid_token.InvalidTokenResponse;

public interface IInvalidTokenService {

    boolean invalidToken(String token);

    InvalidTokenResponse insertInvalidToken(String token);

}

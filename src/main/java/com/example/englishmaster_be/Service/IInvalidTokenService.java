package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Response.InvalidTokenResponse;

public interface IInvalidTokenService {

    boolean invalidToken(String token);

    InvalidTokenResponse insertInvalidToken(String token);

}

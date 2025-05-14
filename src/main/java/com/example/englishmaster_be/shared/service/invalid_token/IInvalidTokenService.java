package com.example.englishmaster_be.shared.service.invalid_token;

import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;

import java.util.List;

public interface IInvalidTokenService {

    boolean inValidToken(String token);

    void insertInvalidToken(SessionActiveEntity sessionActive, InvalidTokenType typeInvalid);

    void insertInvalidTokenList(List<SessionActiveEntity> sessionActiveEntityList, InvalidTokenType typeInvalid);
}

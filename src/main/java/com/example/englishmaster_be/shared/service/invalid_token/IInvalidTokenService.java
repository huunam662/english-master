package com.example.englishmaster_be.shared.service.invalid_token;

import com.example.englishmaster_be.common.constant.InvalidTokenTypeEnum;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;

import java.util.List;

public interface IInvalidTokenService {

    boolean inValidToken(String token);

    void insertInvalidToken(SessionActiveEntity sessionActive, InvalidTokenTypeEnum typeInvalid);

    void insertInvalidTokenList(List<SessionActiveEntity> sessionActiveEntityList, InvalidTokenTypeEnum typeInvalid);
}

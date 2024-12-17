package com.example.englishmaster_be.shared.service.invalid_token;

import com.example.englishmaster_be.common.constant.InvalidTokenTypeEnum;
import com.example.englishmaster_be.model.invalid_token.InvalidTokenEntity;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;

import java.util.List;
import java.util.UUID;

public interface IInvalidTokenService {

    boolean invalidToken(String token);

    InvalidTokenEntity insertInvalidToken(UUID sessionCode, InvalidTokenTypeEnum typeInvalid);

    void insertInvalidTokenList(List<SessionActiveEntity> sessionActiveEntityList, InvalidTokenTypeEnum typeInvalid);
}

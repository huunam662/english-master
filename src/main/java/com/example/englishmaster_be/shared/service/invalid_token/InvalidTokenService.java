package com.example.englishmaster_be.shared.service.invalid_token;

import com.example.englishmaster_be.common.constant.InvalidTokenTypeEnum;
import com.example.englishmaster_be.common.constant.SessionActiveTypeEnum;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.session_active.SessionActiveRepository;
import com.example.englishmaster_be.util.JwtUtil;
import com.example.englishmaster_be.model.invalid_token.InvalidTokenEntity;
import com.example.englishmaster_be.shared.dto.response.invalid_token.InvalidTokenResponse;
import com.example.englishmaster_be.model.invalid_token.InvalidTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidTokenService implements IInvalidTokenService {

    JwtUtil jwtUtil;

    InvalidTokenRepository invalidTokenRepository;

    SessionActiveRepository sessionActiveRepository;


    @Override
    public boolean invalidToken(String token) {
        InvalidTokenEntity tokenExpire = invalidTokenRepository.findById(token).orElse(null);
        return tokenExpire != null;
    }

    @Transactional
    @Override
    public InvalidTokenEntity insertInvalidToken(UUID sessionCode, InvalidTokenTypeEnum typeInvalid) {

        SessionActiveEntity sessionActive = sessionActiveRepository.findByCode(sessionCode);

        LocalDateTime expireTime = jwtUtil.getTokenExpireFromJWT(sessionActive.getToken());

        InvalidTokenEntity invalidToken = InvalidTokenEntity.builder()
                .expireTime(expireTime)
                .token(sessionActive.getToken())
                .type(typeInvalid)
                .user(sessionActive.getUser())
                .build();

        return invalidTokenRepository.save(invalidToken);

    }

    @Transactional
    @Override
    public void insertInvalidTokenList(List<SessionActiveEntity> sessionActiveEntityList, InvalidTokenTypeEnum typeInvalid) {

        sessionActiveEntityList.forEach(sessionActiveEntity -> {

            this.insertInvalidToken(sessionActiveEntity.getCode(), typeInvalid);
        });
    }
}

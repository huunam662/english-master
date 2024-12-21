package com.example.englishmaster_be.shared.invalid_token.service;

import com.example.englishmaster_be.common.constant.InvalidTokenTypeEnum;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.shared.session_active.service.ISessionActiveService;
import com.example.englishmaster_be.util.JwtUtil;
import com.example.englishmaster_be.model.invalid_token.InvalidTokenEntity;
import com.example.englishmaster_be.model.invalid_token.InvalidTokenRepository;
import com.example.englishmaster_be.value.JwtValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidTokenService implements IInvalidTokenService {

    JwtValue jwtValue;

    JwtUtil jwtUtil;

    InvalidTokenRepository invalidTokenRepository;


    @Override
    public boolean inValidToken(String token) {

        String tokenHash = jwtUtil.hashToHex(token);

        Optional<InvalidTokenEntity> tokenExpire = invalidTokenRepository.findById(tokenHash);

        return tokenExpire.isPresent();
    }

    @Transactional
    @Override
    public void insertInvalidToken(SessionActiveEntity sessionActive, InvalidTokenTypeEnum typeInvalid) {

        if(sessionActive == null) return;

        LocalDateTime expireTime = new Date(
                sessionActive.getCreateAt().toInstant(ZoneOffset.UTC).toEpochMilli() + jwtValue.getJwtExpiration()
        ).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        InvalidTokenEntity invalidToken = InvalidTokenEntity.builder()
                .expireTime(expireTime)
                .createAt(LocalDateTime.now(ZoneId.systemDefault()))
                .token(sessionActive.getToken())
                .user(sessionActive.getUser())
                .type(typeInvalid)
                .build();

        invalidTokenRepository.save(invalidToken);

    }

    @Transactional
    @Override
    public void insertInvalidTokenList(List<SessionActiveEntity> sessionActiveEntityList, InvalidTokenTypeEnum typeInvalid) {

        sessionActiveEntityList.forEach(sessionActiveEntity -> {

            this.insertInvalidToken(sessionActiveEntity, typeInvalid);
        });
    }
}

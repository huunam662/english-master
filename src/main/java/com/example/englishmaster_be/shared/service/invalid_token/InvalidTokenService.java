package com.example.englishmaster_be.shared.service.invalid_token;

import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.shared.service.jwt.JwtService;
import com.example.englishmaster_be.model.invalid_token.InvalidTokenEntity;
import com.example.englishmaster_be.model.invalid_token.InvalidTokenRepository;
import com.example.englishmaster_be.shared.service.session_active.ISessionActiveService;
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
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidTokenService implements IInvalidTokenService {

    JwtService jwtUtil;

    InvalidTokenRepository invalidTokenRepository;


    @Override
    public boolean inValidToken(String token) {

        String tokenHash = jwtUtil.hashToHex(token);

        return invalidTokenRepository.isValidToken(tokenHash);
    }


    @Transactional
    @Override
    public void saveInvalidToken(String jwtToken, UUID userId, InvalidTokenType typeInvalid) {

        if(jwtToken == null) return;

        if(userId == null) return;

        String tokenHash = jwtUtil.hashToHex(jwtToken);

        if(invalidTokenRepository.isValidToken(tokenHash))
            return;

        invalidTokenRepository.insertInvalidToken(
                tokenHash, LocalDateTime.now(), LocalDateTime.now(), typeInvalid.name(), userId
        );
    }

    @Override
    public void sessionActiveToInvalidToken(String hashToken, UUID userId, InvalidTokenType typeInvalid) {

        if(hashToken == null) return;

        if(userId == null) return;

        if(invalidTokenRepository.isValidToken(hashToken))
            return;

        invalidTokenRepository.insertInvalidToken(
                hashToken, LocalDateTime.now(), LocalDateTime.now(), typeInvalid.name(), userId
        );
    }

    @Transactional
    @Override
    public void saveInvalidTokenList(List<SessionActiveEntity> sessionActiveEntityList, UserEntity user, InvalidTokenType typeInvalid) {

        sessionActiveEntityList.forEach(sessionActive -> {

            String tokenHash = sessionActive.getToken();

            sessionActiveToInvalidToken(tokenHash, user.getUserId(), typeInvalid);
        });
    }

}

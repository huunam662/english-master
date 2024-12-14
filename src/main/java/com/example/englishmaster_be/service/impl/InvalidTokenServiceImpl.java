package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.config.jwt.JwtUtil;
import com.example.englishmaster_be.entity.InvalidTokenEntity;
import com.example.englishmaster_be.model.response.InvalidTokenResponse;
import com.example.englishmaster_be.repository.InvalidTokenRepository;
import com.example.englishmaster_be.service.IInvalidTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidTokenServiceImpl implements IInvalidTokenService {

    JwtUtil jwtUtils;

    InvalidTokenRepository invalidTokenRepository;


    @Override
    public boolean invalidToken(String token) {
        InvalidTokenEntity tokenExpire = invalidTokenRepository.findById(token).orElse(null);
        return tokenExpire != null;
    }

    @Override
    public InvalidTokenResponse insertInvalidToken(String token) {

        // Kiểm tra và trích xuất thời gian hết hạn của token
        LocalDateTime dateExpire = jwtUtils.getTokenExpireFromJWT(token);
        if (dateExpire == null) {
            throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn.");
        }

        // Tạo đối tượng InvalidTokenEntity và lưu vào cơ sở dữ liệu
        InvalidTokenEntity invalidToken = InvalidTokenEntity.builder()
                .expireTime(dateExpire)
                .token(token)
                .build();
        invalidTokenRepository.save(invalidToken);

        // Trả về phản hồi với thông tin token đã lưu
        return new InvalidTokenResponse(invalidToken);

    }

}

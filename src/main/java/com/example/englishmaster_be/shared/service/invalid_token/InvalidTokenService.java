package com.example.englishmaster_be.shared.service.invalid_token;

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
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidTokenService implements IInvalidTokenService {

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

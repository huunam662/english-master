package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.config.jwt.util.JwtUtil;
import com.example.englishmaster_be.model.InvalidToken;
import com.example.englishmaster_be.model.response.InvalidTokenResponse;
import com.example.englishmaster_be.repository.InvalidTokenRepository;
import com.example.englishmaster_be.service.IInvalidTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@AllArgsConstructor
public class InvalidTokenServiceImpl implements IInvalidTokenService {

    InvalidTokenRepository invalidTokenRepository;

    JwtUtil jwtUtils;

    @Override
    public boolean verifyToken(String token) {
        InvalidToken tokenExpire = invalidTokenRepository.findById(token).orElse(null);
        return tokenExpire != null;
    }

    @Override
    public InvalidTokenResponse insertInvalidToken(String token) {
        try {
            // Kiểm tra và trích xuất thời gian hết hạn của token
            LocalDateTime dateExpire = jwtUtils.getTokenExpiryFromJWT(token);
            if (dateExpire == null) {
                throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn.");
            }

            // Tạo đối tượng InvalidToken và lưu vào cơ sở dữ liệu
            InvalidToken invalidToken = InvalidToken.builder()
                    .expireTime(dateExpire)
                    .token(token)
                    .build();
            invalidTokenRepository.save(invalidToken);

            // Trả về phản hồi với thông tin token đã lưu
            return new InvalidTokenResponse(invalidToken);

        } catch (Exception e) {
            // Xử lý ngoại lệ và ghi log nếu cần thiết
            // Ví dụ: logger.error("Lỗi khi chèn token không hợp lệ: ", e);
            throw new RuntimeException("Có lỗi xảy ra khi chèn token không hợp lệ.", e);
        }
    }

}

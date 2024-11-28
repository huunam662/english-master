package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Configuration.jwt.JwtUtils;
import com.example.englishmaster_be.Model.InvalidToken;
import com.example.englishmaster_be.Model.Response.InvalidTokenResponse;
import com.example.englishmaster_be.Repository.InvalidTokenRepository;
import com.example.englishmaster_be.Service.IInvalidTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class InvalidTokenServiceImpl implements IInvalidTokenService {

    InvalidTokenRepository invalidTokenRepository;

    JwtUtils jwtUtils;

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

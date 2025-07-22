package com.example.englishmaster_be.domain.user.auth.repository;

import com.example.englishmaster_be.common.constant.OtpStatus;
import com.example.englishmaster_be.domain.user.auth.model.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;


public interface OtpRepository extends JpaRepository<OtpEntity, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM OtpEntity o WHERE o.email = :email AND o.status = :status")
    void deleteByEmailAndStatus(@Param("email") String email, @Param("status") OtpStatus status);

    @Transactional
    @Modifying
    @Query(value = """
        DELETE FROM otp WHERE email = :email
    """, nativeQuery = true)
    void deleteByEmail(@Param("email") String email);

    @Query("""
        SELECT o FROM OtpEntity o
        WHERE o.otp = :otp
    """)
    Optional<OtpEntity> findByOtp(@Param("otp") String otp);

    Optional<OtpEntity> findByEmailAndOtp(String email, String otp);

    @Transactional
    @Modifying
    @Query(value = """
        UPDATE otp SET status = :status
        WHERE otp = :otpCode AND email = :email
    """, nativeQuery = true)
    void updateStatus(
            @Param("email") String email,
            @Param("otpCode") String otpCode,
            @Param("status") String status
    );

    @Query(value = """
        SELECT o.status = "VERIFIED" OR o.status = "USED"
        FROM otp o
        WHERE o.otp = :otpCode AND o.email = :email
    """, nativeQuery = true)
    boolean isVerified(@Param("otpCode") String otpCode, @Param("email") String email);

    @Query("""
        SELECT o FROM OtpEntity o
        INNER JOIN FETCH o.user u
        WHERE o.otp = :otpCode AND o.email = u.email
    """)
    Optional<OtpEntity> findOtpJoinUserByOtpCode(@Param("otpCode") String otpCode);
}

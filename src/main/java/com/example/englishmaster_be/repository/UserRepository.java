package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    // Lấy danh sách người dùng lâu không đăng nhập
    @Query("SELECT u FROM UserEntity u WHERE u.lastLogin < :cutoffDate")
    List<UserEntity> findUsersNotLoggedInSince(@Param("cutoffDate") LocalDateTime cutoffDate);
}


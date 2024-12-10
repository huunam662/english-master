package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Lấy danh sách người dùng lâu không đăng nhập
    @Query("SELECT u FROM User u WHERE u.lastLogin < :cutoffDate")
    List<User> findUsersNotLoggedInSince(@Param("cutoffDate") LocalDateTime cutoffDate);
}


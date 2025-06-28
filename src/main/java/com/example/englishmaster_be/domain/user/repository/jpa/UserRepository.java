package com.example.englishmaster_be.domain.user.repository.jpa;

import com.example.englishmaster_be.domain.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    @Query(value = """
        SELECT EXISTS(SELECT email FROM users WHERE email = :email)
    """, nativeQuery = true)
    boolean existsByEmail(String email);

    // Lấy danh sách người dùng lâu không đăng nhập
    @Query("SELECT u FROM UserEntity u WHERE u.lastLogin < :cutoffDate")
    List<UserEntity> findUsersNotLoggedInSince(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("""
        SELECT u, r FROM UserEntity u
        INNER JOIN RoleEntity r ON u.role.roleId = r.roleId
        WHERE u.email = :email
    """)
    Optional<UserEntity> findUserJoinRoleByEmail(@Param("email") String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET last_login = :lastLogin WHERE id = :userId", nativeQuery = true)
    void updateLastLogin(@Param("userId") UUID userId, @Param("lastLogin") LocalDateTime lastLogin);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET is_enabled = TRUE WHERE id = :userId", nativeQuery = true)
    void updateIsEnabled(@Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query(value = """
        UPDATE users SET password = :newPassword WHERE email = :email
    """, nativeQuery = true)
    void updatePassword(@Param("newPassword") String newPassword, @Param("email") String email);

    @Query(value = """
        SELECT id FROM users WHERE email IN :emails
    """, nativeQuery = true)
    List<UUID> findAllIdIn(@Param("emails") List<String> emails);
}


package com.example.englishmaster_be.ScheduledTask;

import com.example.englishmaster_be.entity.InvalidTokenEntity;
import com.example.englishmaster_be.entity.QInvalidTokenEntity;
import com.example.englishmaster_be.entity.QUserEntity;
import com.example.englishmaster_be.entity.UserEntity;
import com.example.englishmaster_be.Repository.ConfirmationTokenRepository;
import com.example.englishmaster_be.Repository.InvalidTokenRepository;
import com.example.englishmaster_be.Repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Component
public class ScheduledTasks {

    UserRepository userRepository;

    ConfirmationTokenRepository confirmationTokenRepository;

    JPAQueryFactory queryFactory;

    InvalidTokenRepository invalidTokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 1 * * ?") // Run at 1 AM every day
    public void deleteExpiredUsers() {
        log.info("Starting deleteExpiredUsers task");
        try {
            QUserEntity qUser = QUserEntity.userEntity;
            LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(5);

            List<UserEntity> usersToDelete = queryFactory.selectFrom(qUser)
                    .where(qUser.enabled.eq(false)
                            .and(qUser.createAt.before(expirationTime)))
                    .fetch();

            for (UserEntity user : usersToDelete) {
                confirmationTokenRepository.deleteAll(confirmationTokenRepository.findByUserId(user.getUserId()));
                userRepository.delete(user);
            }

            log.info("Deleted {} expired users", usersToDelete.size());
        } catch (Exception e) {
            log.error("Error in deleteExpiredUsers task", e);
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * ?") // Run at 2 AM every day
    public void deleteExpiredToken() {
        log.info("Starting deleteExpiredToken task");
        try {
            QInvalidTokenEntity qInvalidToken = QInvalidTokenEntity.invalidTokenEntity;
            LocalDateTime expirationTime = LocalDateTime.now();

            List<InvalidTokenEntity> tokensToDelete = queryFactory.selectFrom(qInvalidToken)
                    .where(qInvalidToken.expireTime.before(expirationTime)).fetch();

            invalidTokenRepository.deleteAll(tokensToDelete);

            log.info("Deleted {} expired tokens", tokensToDelete.size());
        } catch (Exception e) {
            log.error("Error in deleteExpiredToken task", e);
        }
    }
}
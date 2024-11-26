package com.example.englishmaster_be.scheduledTask;

import com.example.englishmaster_be.model.InvalidToken;
import com.example.englishmaster_be.model.QInvalidToken;
import com.example.englishmaster_be.model.QUser;
import com.example.englishmaster_be.model.User;
import com.example.englishmaster_be.repository.ConfirmationTokenRepository;
import com.example.englishmaster_be.repository.InvalidTokenRepository;
import com.example.englishmaster_be.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private InvalidTokenRepository invalidTokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 1 * * ?") // Run at 1 AM every day
    public void deleteExpiredUsers() {
        logger.info("Starting deleteExpiredUsers task");
        try {
            QUser qUser = QUser.user;
            LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(5);

            List<User> usersToDelete = queryFactory.selectFrom(qUser)
                    .where(qUser.isEnabled.eq(false)
                            .and(qUser.createAt.before(expirationTime)))
                    .fetch();

            for (User user : usersToDelete) {
                confirmationTokenRepository.deleteAll(confirmationTokenRepository.findByUserId(user.getUserId()));
                userRepository.delete(user);
            }

            logger.info("Deleted {} expired users", usersToDelete.size());
        } catch (Exception e) {
            logger.error("Error in deleteExpiredUsers task", e);
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * ?") // Run at 2 AM every day
    public void deleteExpiredToken() {
        logger.info("Starting deleteExpiredToken task");
        try {
            QInvalidToken qInvalidToken = QInvalidToken.invalidToken;
            LocalDateTime expirationTime = LocalDateTime.now();

            List<InvalidToken> tokensToDelete = queryFactory.selectFrom(qInvalidToken)
                    .where(qInvalidToken.expireTime.before(expirationTime)).fetch();

            invalidTokenRepository.deleteAll(tokensToDelete);

            logger.info("Deleted {} expired tokens", tokensToDelete.size());
        } catch (Exception e) {
            logger.error("Error in deleteExpiredToken task", e);
        }
    }
}
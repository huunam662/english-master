package com.example.englishmaster_be.ScheduledTask;

import com.example.englishmaster_be.Model.QUser;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.ConfirmationTokenRepository;
import com.example.englishmaster_be.Repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Transactional
    @Scheduled(fixedRate = 10000000)
    public void deleteExpiredUsers() {
        QUser qUser = QUser.user;
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(5);

        // Select users who are not enabled and have expired tokens
        List<User> usersToDelete = queryFactory.selectFrom(qUser)
                .where(qUser.isEnabled.eq(false)
                        .and(qUser.createAt.before(expirationTime)))
                .fetch();

        // Delete confirmation tokens and users
        for (User user : usersToDelete) {
            confirmationTokenRepository.deleteAll(confirmationTokenRepository.findByUserId(user.getUserId()));
            userRepository.delete(user);
        }
    }
}

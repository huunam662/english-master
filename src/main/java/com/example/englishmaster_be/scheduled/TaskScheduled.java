package com.example.englishmaster_be.scheduled;

import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.domain.user.auth.model.InvalidTokenEntity;
import com.example.englishmaster_be.domain.user.auth.model.QInvalidTokenEntity;
import com.example.englishmaster_be.domain.user.auth.model.QSessionActiveEntity;
import com.example.englishmaster_be.domain.user.auth.model.SessionActiveEntity;
import com.example.englishmaster_be.domain.user.user.model.QUserEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.auth.repository.SessionActiveRepository;
import com.example.englishmaster_be.domain.user.auth.repository.InvalidTokenRepository;
import com.example.englishmaster_be.domain.user.user.repository.UserRepository;
import com.example.englishmaster_be.domain.user.auth.service.invalid_token.IInvalidTokenService;
import com.example.englishmaster_be.value.JwtValue;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Slf4j
@EnableScheduling
@Component
public class TaskScheduled {

    private final JwtValue jwtValue;

    private final JPAQueryFactory queryFactory;

    private final IInvalidTokenService invalidTokenService;

    private final UserRepository userRepository;

    private final InvalidTokenRepository invalidTokenRepository;

    private final SessionActiveRepository sessionActiveRepository;

    public TaskScheduled(JwtValue jwtValue, JPAQueryFactory queryFactory, IInvalidTokenService invalidTokenService, UserRepository userRepository, InvalidTokenRepository invalidTokenRepository, SessionActiveRepository sessionActiveRepository) {
        this.jwtValue = jwtValue;
        this.queryFactory = queryFactory;
        this.invalidTokenService = invalidTokenService;
        this.userRepository = userRepository;
        this.invalidTokenRepository = invalidTokenRepository;
        this.sessionActiveRepository = sessionActiveRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 3 * * ?") // Run at 3 AM every day
    public void scheduledRunTask(){

        deleteExpiredUsers();
        deleteInvalidToken();
        deleteAllSessionConfirm();
        invalidTokenExpired();
    }

    @Transactional
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
                sessionActiveRepository.deleteAll(sessionActiveRepository.findByUserId(user.getUserId()));
                userRepository.delete(user);
            }

            log.info("Deleted {} expired users", usersToDelete.size());
        } catch (Exception e) {
            log.error("Error in deleteExpiredUsers task", e);
        }
    }

    @Transactional
    public void deleteInvalidToken(){

        log.info("Starting deleteInvalidToken task");

        try{

            JPAQuery<InvalidTokenEntity> query = queryFactory
                    .selectFrom(QInvalidTokenEntity.invalidTokenEntity)
                    .where(
                            Expressions.booleanTemplate(
                                    "{0} <= {1}",
                                    QInvalidTokenEntity.invalidTokenEntity.createAt,
                                    LocalDateTime.now().minusDays(3)
                            )
                    );

            List<InvalidTokenEntity> tokensToDelete = query.fetch();

            invalidTokenRepository.deleteAll(tokensToDelete);

        }
        catch (Exception e){
            log.error("ErrorEnum in deleteInvalidToken task", e);
        }
    }

    @Transactional
    public void deleteAllSessionConfirm(){

        log.info("Starting deleteAllSessionActiveConfirm task");

        try {

            JPAQuery<SessionActiveEntity> query = queryFactory
                    .selectFrom(QSessionActiveEntity.sessionActiveEntity)
                    .where(
                            QSessionActiveEntity.sessionActiveEntity.type.eq(SessionActiveType.CONFIRM).or(
                                    QSessionActiveEntity.sessionActiveEntity.token.isNull()
                            )
                    );

            List<SessionActiveEntity> sessionActiveEntityList = query.fetch();

            sessionActiveRepository.deleteAll(sessionActiveEntityList);

        }
        catch (Exception e){
            log.error("Error in deleteAllSessionActiveConfirm task", e);
        }
    }


    @Transactional
    public void invalidTokenExpired(){

        log.info("Starting invalidTokenExpired task");

        try {

            JPAQuery<SessionActiveEntity> query = queryFactory
                    .selectFrom(QSessionActiveEntity.sessionActiveEntity)
                    .where(
                        Expressions.booleanTemplate(
                                "{0} <= {1}",
                                QSessionActiveEntity.sessionActiveEntity.createAt,
                                new Date(System.currentTimeMillis() - jwtValue.getJwtExpiration())
                                        .toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime()
                        ).and(
                                QSessionActiveEntity.sessionActiveEntity.type.eq(SessionActiveType.CONFIRM).not()
                        ).and(
                                QSessionActiveEntity.sessionActiveEntity.token.isNotNull()
                        )
                    );

            List<SessionActiveEntity> sessionActiveEntityList = query.fetch();

            sessionActiveEntityList.forEach(
                    sessionActiveEntity -> invalidTokenService.sessionActiveToInvalidToken(sessionActiveEntity.getToken(), sessionActiveEntity.getUser().getUserId(), InvalidTokenType.EXPIRED)
            );

            sessionActiveRepository.deleteAll(sessionActiveEntityList);
        }
        catch (Exception e){
            log.error("Error in invalidTokenExpired task", e);
        }
    }

}
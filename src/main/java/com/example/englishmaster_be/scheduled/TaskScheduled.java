package com.example.englishmaster_be.scheduled;

import com.example.englishmaster_be.common.constant.InvalidTokenTypeEnum;
import com.example.englishmaster_be.common.constant.SessionActiveTypeEnum;
import com.example.englishmaster_be.model.invalid_token.InvalidTokenEntity;
import com.example.englishmaster_be.model.invalid_token.QInvalidTokenEntity;
import com.example.englishmaster_be.model.session_active.QSessionActiveEntity;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.user.QUserEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.model.session_active.SessionActiveRepository;
import com.example.englishmaster_be.model.invalid_token.InvalidTokenRepository;
import com.example.englishmaster_be.model.user.UserRepository;
import com.example.englishmaster_be.shared.invalid_token.service.IInvalidTokenService;
import com.example.englishmaster_be.value.JwtValue;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Slf4j
@Component
public class TaskScheduled {

    JwtValue jwtValue;

    JPAQueryFactory queryFactory;

    IInvalidTokenService invalidTokenService;

    UserRepository userRepository;

    InvalidTokenRepository invalidTokenRepository;

    SessionActiveRepository sessionActiveRepository;



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
                sessionActiveRepository.deleteAll(sessionActiveRepository.findByUserId(user.getUserId()));
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


    @Transactional
    @Scheduled(cron = "0 0 3 * * ?") // Run at 3 AM every day
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
    @Scheduled(cron = "0 0 3 * * ?") // Run at 3 AM every day
    public void deleteAllSessionConfirm(){

        log.info("Starting deleteAllSessionActiveConfirm task");

        try {

            JPAQuery<SessionActiveEntity> query = queryFactory
                    .selectFrom(QSessionActiveEntity.sessionActiveEntity)
                    .where(
                            QSessionActiveEntity.sessionActiveEntity.type.eq(SessionActiveTypeEnum.CONFIRM)
                    );

            List<SessionActiveEntity> sessionActiveEntityList = query.fetch();

            sessionActiveRepository.deleteAll(sessionActiveEntityList);

        }
        catch (Exception e){
            log.error("Error in deleteAllSessionActiveConfirm task", e);
        }
    }


    @Transactional
    @Scheduled(cron = "0 0 3 * * ?")
    public void invalidTokenExpired(){

        log.info("Starting invalidTokenExpired task");

        try {

            JPAQuery<SessionActiveEntity> query = queryFactory
                    .selectFrom(QSessionActiveEntity.sessionActiveEntity)
                    .where(
                        Expressions.booleanTemplate(
                                "{0} <= {1}",
                                QInvalidTokenEntity.invalidTokenEntity.createAt,
                                new Date(System.currentTimeMillis() - jwtValue.getJwtExpiration())
                                        .toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime()
                        )
                    );

            List<SessionActiveEntity> sessionActiveEntityList = query.fetch();

            invalidTokenService.insertInvalidTokenList(sessionActiveEntityList, InvalidTokenTypeEnum.EXPIRED);

        }
        catch (Exception e){
            log.error("Error in invalidTokenExpired task", e);
        }
    }
}
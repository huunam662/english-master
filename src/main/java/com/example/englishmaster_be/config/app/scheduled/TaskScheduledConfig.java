package com.example.englishmaster_be.config.app.scheduled;

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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Slf4j(topic = "TASK-SCHEDULED-CONFIG")
@EnableScheduling
@Configuration
public class TaskScheduledConfig {

    @PersistenceContext
    private final EntityManager em;

    private final JwtValue jwtValue;
    private final IInvalidTokenService invalidTokenService;
    private final UserRepository userRepository;
    private final InvalidTokenRepository invalidTokenRepository;
    private final SessionActiveRepository sessionActiveRepository;

    public TaskScheduledConfig(EntityManager em, JwtValue jwtValue, IInvalidTokenService invalidTokenService, UserRepository userRepository, InvalidTokenRepository invalidTokenRepository, SessionActiveRepository sessionActiveRepository) {
        this.jwtValue = jwtValue;
        this.invalidTokenService = invalidTokenService;
        this.userRepository = userRepository;
        this.invalidTokenRepository = invalidTokenRepository;
        this.sessionActiveRepository = sessionActiveRepository;
        this.em = em;
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
            List<UserEntity> usersToDelete = new JPAQuery<UserEntity>(em)
                    .from(qUser)
                    .where(qUser.enabled.eq(false))
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
        QInvalidTokenEntity qInvalidTokenEntity = QInvalidTokenEntity.invalidTokenEntity;
        try{
            List<InvalidTokenEntity> tokensToDelete = new JPAQuery<InvalidTokenEntity>(em)
                    .from(qInvalidTokenEntity)
                    .where(
                            Expressions.booleanTemplate(
                                    "{0} <= {1}",
                                    qInvalidTokenEntity.createAt,
                                    LocalDateTime.now().minusDays(3)
                            )
                    ).fetch();
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
            QSessionActiveEntity qsessionActiveEntity = QSessionActiveEntity.sessionActiveEntity;
            List<SessionActiveEntity> sessionActiveEntityList = new JPAQuery<SessionActiveEntity>(em)
                    .from(qsessionActiveEntity)
                    .where(
                            qsessionActiveEntity.type.eq(SessionActiveType.CONFIRM).or(
                                    qsessionActiveEntity.token.isNull()
                            )
                    )
                    .fetch();
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
            QSessionActiveEntity qSessionActiveEntity = QSessionActiveEntity.sessionActiveEntity;
            List<SessionActiveEntity> sessionActiveEntityList = new JPAQuery<SessionActiveEntity>(em)
                    .from(qSessionActiveEntity)
                    .where(
                        Expressions.booleanTemplate(
                                "{0} <= {1}",
                                qSessionActiveEntity.createAt,
                                new Date(System.currentTimeMillis() - jwtValue.getJwtExpiration())
                                        .toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime()
                    )
                    .and(qSessionActiveEntity.type.eq(SessionActiveType.CONFIRM).not())
                    .and(qSessionActiveEntity.token.isNotNull()))
                    .fetch();
            sessionActiveEntityList.forEach(sessionActiveEntity -> invalidTokenService.sessionActiveToInvalidToken(sessionActiveEntity.getToken(), sessionActiveEntity.getUser().getUserId(), InvalidTokenType.EXPIRED));
            sessionActiveRepository.deleteAll(sessionActiveEntityList);
        }
        catch (Exception e){
            log.error("Error in invalidTokenExpired task", e);
        }
    }

}
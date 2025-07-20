package com.example.englishmaster_be.domain.user.admin.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.user.user.model.QUserEntity;
import com.example.englishmaster_be.domain.mock_test.mock_test.service.IMockTestService;
import com.example.englishmaster_be.domain.exam.pack.pack.service.IPackService;
import com.example.englishmaster_be.domain.exam.topic.topic.service.ITopicService;
import com.example.englishmaster_be.domain.user.user.dto.view.IUserPageView;
import com.example.englishmaster_be.domain.user.user.repository.UserDslRepository;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import com.example.englishmaster_be.domain.mock_test.mock_test.model.MockTestEntity;
import com.example.englishmaster_be.domain.exam.pack.pack.model.PackEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.user.repository.UserRepository;
import com.example.englishmaster_be.domain.user.admin.dto.res.CountMockTestTopicRes;
import com.example.englishmaster_be.domain.user.auth.service.mailer.MailerService;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.mail.MessagingException;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class AdminService implements IAdminService {

    private final JPAQueryFactory queryFactory;
    private final MailerService mailerUtil;
    private final IUserService userService;
    private final IPackService packService;
    private final ITopicService topicService;
    private final IMockTestService mockTestService;
    private final UserRepository userRepository;
    private final UserDslRepository userSpecRepository;

    @Lazy
    public AdminService(UserDslRepository userSpecRepository, JPAQueryFactory queryFactory, MailerService mailerUtil, IUserService userService, IPackService packService, ITopicService topicService, IMockTestService mockTestService, UserRepository userRepository) {
        this.queryFactory = queryFactory;
        this.mailerUtil = mailerUtil;
        this.userService = userService;
        this.packService = packService;
        this.topicService = topicService;
        this.mockTestService = mockTestService;
        this.userRepository = userRepository;
        this.userSpecRepository = userSpecRepository;
    }

    @Transactional
    @Override
    public void deleteUser(UUID userId) {

        UserEntity userEntity = userService.getUserById(userId);

        userRepository.delete(userEntity);
    }


    @Override
    public Page<IUserPageView> getPageUser(PageOptionsReq optionsReq) {
        return userSpecRepository.findPageUser(optionsReq);
    }


    @Transactional
    @Override
    public void enableUser(UUID userId, Boolean enable) {

        if(enable == null)
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "The enable parameter is required");

        UserEntity user = userService.getUserById(userId);

        user.setEnabled(enable);
        userRepository.save(user);
    }

    @Override
    public List<CountMockTestTopicRes> getCountMockTestOfTopic(String date, UUID packId) {

        PackEntity pack = packService.getPackById(packId);

        List<TopicEntity> listTopic = topicService.getAllTopicToPack(pack);

        return listTopic.stream().map(topic -> {

            List<MockTestEntity> mockTests;

            if(date == null) mockTests = mockTestService.getAllMockTestToTopic(topic);
            else{
                String[] str = date.split("-");
                String day = null, year, month = null;
                year = str[0];

                if (str.length > 1) {
                    month = str[1];
                    if (str.length > 2) {
                        day = str[2];
                    }
                }

                mockTests = mockTestService.getAllMockTestByYearMonthAndDay(topic, year, month, day);
            }
            CountMockTestTopicRes res = new CountMockTestTopicRes();
            res.setTopicName(topic.getTopicName());
            res.setCountMockTest(mockTests.size());
            return res;
        }).toList();
    }

    @Override
    public List<UserEntity> getUsersNotLoggedInLast10Days() {

        // Tính thời gian cách đây 10 ngày
        LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10);

        // Xây dựng điều kiện truy vấn
        BooleanExpression condition = QUserEntity.userEntity.lastLogin.before(tenDaysAgo);

        // Thực hiện truy vấn với QueryDSL
        JPAQuery<UserEntity> query = queryFactory
                .selectFrom(QUserEntity.userEntity)
                .where(condition);

        // Chuyển đổi sang DTO để trả về
        return query.fetch();
    }


    @Transactional
    @Override
    public List<UserEntity> findUsersInactiveForDaysAndNotify(int inactiveDays) {

        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(inactiveDays);

        BooleanExpression wherePattern = QUserEntity.userEntity.lastLogin.before(thresholdDate);

        JPAQuery<UserEntity> query = queryFactory
                .selectFrom(QUserEntity.userEntity)
                .where(wherePattern);

        List<UserEntity> inactiveUsers = query.fetch();

        for (UserEntity user : inactiveUsers)
            mailerUtil.sendNotificationEmail(user.getUserId());

        return inactiveUsers;
    }

    @Override
    public void notifyInactiveUsers(){
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(10);

        List<UserEntity> inactiveUsers = userRepository.findUsersNotLoggedInSince(cutoffDate);

        for (UserEntity user : inactiveUsers) {
            try {
                mailerUtil.sendMail(user.getEmail());
            }catch (MessagingException e){
                System.out.println("Failed to send email to: " + user.getEmail());
            }
        }
    }


    @Transactional
    @Override
    public List<UserEntity> findUsersInactiveForDays(int inactiveDays) {

        // Tính toán ngày đã qua kể từ ngày hiện tại
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(inactiveDays);

        // Xây dựng điều kiện lọc
        BooleanExpression wherePattern = QUserEntity.userEntity.lastLogin.before(thresholdDate);

        // Truy vấn người dùng lâu ngày chưa đăng nhập
        JPAQuery<UserEntity> query = queryFactory
                .selectFrom(QUserEntity.userEntity)
                .where(wherePattern);

        // Chuyển đổi kết quả thành UserRes
        return query.fetch();
    }
}

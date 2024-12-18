package com.example.englishmaster_be.domain.admin.service;

import com.example.englishmaster_be.common.constant.RoleEnum;
import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.common.thread.MessageResponseHolder;
import com.example.englishmaster_be.domain.mock_test.service.IMockTestService;
import com.example.englishmaster_be.domain.pack.service.IPackService;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.user.dto.request.UserFilterRequest;
import com.example.englishmaster_be.domain.user.dto.response.UserResponse;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.mapper.UserMapper;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.pack.PackEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.QUserEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.model.user.UserRepository;
import com.example.englishmaster_be.domain.admin.dto.response.CountMockTestTopicResponse;
import com.example.englishmaster_be.util.MailerUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
public class AdminService implements IAdminService {

    JPAQueryFactory queryFactory;

    MailerUtil mailerUtil;

    IUserService userService;

    IPackService packService;

    ITopicService topicService;

    IMockTestService mockTestService;

    UserRepository userRepository;



    @Transactional
    @Override
    public void deleteUser(UUID userId) {

        UserEntity userEntity = userService.getUserById(userId);

        userRepository.delete(userEntity);
    }


    @Override
    public FilterResponse<?> getAllUser(UserFilterRequest filterRequest) {

        FilterResponse<UserResponse> filterResponse = FilterResponse.<UserResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getSize())
                .build();

        BooleanExpression wherePattern = QUserEntity.userEntity.role.roleName.eq(RoleEnum.ADMIN);

        if (filterRequest.getEnable() != null)
            wherePattern.and(QUserEntity.userEntity.enabled.eq(filterRequest.getEnable()));

        long totalElements = Optional.ofNullable(
                queryFactory
                        .select(QUserEntity.userEntity.count())
                        .from(QUserEntity.userEntity)
                        .where(wherePattern)
                        .fetchOne()
        ).orElse(0L);

        long totalPages = (long) Math.ceil((double) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalPages(totalPages);

        OrderSpecifier<?> orderSpecifier;

        if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
            orderSpecifier = QUserEntity.userEntity.updateAt.desc();
        else
            orderSpecifier = QUserEntity.userEntity.updateAt.asc();

        JPAQuery<UserEntity> query = queryFactory
                .selectFrom(QUserEntity.userEntity)
                .where(wherePattern)
                .orderBy(orderSpecifier)
                .offset(filterResponse.getOffset())
                .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                UserMapper.INSTANCE.toUserResponseList(query.fetch())
        );

        return filterResponse;
    }


    @Transactional
    @Override
    public void enableUser(UUID userId, Boolean enable) {

        if(enable == null)
            throw new BadRequestException("The enable parameter is required");

        UserEntity user = userService.getUserById(userId);

        user.setEnabled(enable);
        userRepository.save(user);

        if (enable)
            MessageResponseHolder.setMessage("Enable account of UserEntity successfully");
        else
            MessageResponseHolder.setMessage("Disable account of UserEntity successfully");

    }

    @Override
    public List<CountMockTestTopicResponse> getCountMockTestOfTopic(String date, UUID packId) {

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

            return CountMockTestTopicResponse.builder()
                    .topicName(topic.getTopicName())
                    .countMockTest(mockTests.size())
                    .build();
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
            mailerUtil.sendNotificationEmail(user);

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

        // Chuyển đổi kết quả thành UserResponse
        return query.fetch();
    }
}

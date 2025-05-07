package com.example.englishmaster_be.domain.user.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.Role;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.exam.dto.response.ExamResultResponse;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
import com.example.englishmaster_be.domain.user.dto.request.*;
import com.example.englishmaster_be.mapper.MockTestMapper;
import com.example.englishmaster_be.mapper.TopicMapper;
import com.example.englishmaster_be.mapper.UserMapper;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.mock_test.QMockTestEntity;
import com.example.englishmaster_be.model.topic.QTopicEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.model.user.UserRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {

    JPAQueryFactory queryFactory;

    UserRepository userRepository;

    IUploadService uploadService;



    @Transactional
    @Override
    @SneakyThrows
    public UserEntity changeProfile(UserChangeProfileRequest changeProfileRequest) {

        UserEntity user = currentUser();

        UserMapper.INSTANCE.flowToUserEntity(changeProfileRequest, user);

        if(changeProfileRequest.getAvatar() != null && !changeProfileRequest.getAvatar().isEmpty()){

            if(user.getAvatar() != null && !user.getAvatar().isEmpty())
                uploadService.delete(
                        FileDeleteRequest.builder()
                                .filepath(user.getAvatar())
                                .build()
                );

            user.setAvatar(changeProfileRequest.getAvatar());
        }

        return userRepository.save(user);
    }



    @Override
    public FilterResponse<?> getExamResultsUser(UserFilterRequest filterRequest) {

        FilterResponse<ExamResultResponse> filterResponse = FilterResponse.<ExamResultResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getSize())
                .build();

        UserEntity user = currentUser();

        JPAQuery<TopicEntity> queryMockTest = queryFactory.select(QMockTestEntity.mockTestEntity.topic)
                .from(QMockTestEntity.mockTestEntity)
                .where(QMockTestEntity.mockTestEntity.user.userId.eq(user.getUserId()))
                .groupBy(QMockTestEntity.mockTestEntity.topic);

        List<TopicEntity> listTopicUser = queryMockTest.fetch();

        BooleanExpression wherePatternOfTopic = QTopicEntity.topicEntity.in(listTopicUser);

        long totalElements = Optional.ofNullable(
                queryFactory.select(QTopicEntity.topicEntity.count())
                        .from(QTopicEntity.topicEntity)
                        .where(wherePatternOfTopic)
                        .fetchOne()
        ).orElse(0L);
        long totalPages = (long) Math.ceil((double) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalPages(totalPages);

        OrderSpecifier<?> orderSpecifier;

        if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
            orderSpecifier = QTopicEntity.topicEntity.updateAt.desc();
        else orderSpecifier = QTopicEntity.topicEntity.updateAt.asc();

        JPAQuery<TopicEntity> query = queryFactory
                .selectFrom(QTopicEntity.topicEntity)
                .where(wherePatternOfTopic)
                .orderBy(orderSpecifier)
                .offset(filterResponse.getOffset())
                .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                query.fetch().stream().map(
                        topic -> {
                            ExamResultResponse examResultResponse = ExamResultResponse.builder()
                                    .topic(TopicMapper.INSTANCE.toTopicResponse(topic))
                                    .build();

                            JPAQuery<MockTestEntity> queryListMockTest = queryFactory.selectFrom(QMockTestEntity.mockTestEntity)
                                    .where(QMockTestEntity.mockTestEntity.user.userId.eq(user.getUserId()))
                                    .where(QMockTestEntity.mockTestEntity.topic.eq(topic));

                            examResultResponse.setListMockTest(
                                    MockTestMapper.INSTANCE.toMockTestResponseList(queryListMockTest.fetch())
                            );

                            return examResultResponse;
                        }
                ).toList()
        );

        return filterResponse;
    }


    @Override
    public UserEntity getUserById(UUID userId) {

        return userRepository.findById(userId).orElseThrow(
                () -> new ErrorHolder(Error.BAD_REQUEST, "User not existed.")
        );
    }

    @Override
    public UserEntity currentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails)
            return getUserByEmail(userDetails.getUsername());

        throw new AuthenticationServiceException("Please authenticate first.");
    }

    @Override
    public Boolean currentUserIsAdmin() {

        UserEntity currentUser = currentUser();

        return currentUser.getRole()
                .getRoleName()
                .equals(Role.ADMIN);
    }

    @Override
    public UserEntity getUserByEmail(String email) {

        return userRepository.findByEmail(email).orElseThrow(
                () -> new ErrorHolder(Error.BAD_REQUEST, "User not existed.")
        );
    }


    @Override
    public boolean existsEmail(String email) {

        return userRepository.existsByEmail(email);
    }

}

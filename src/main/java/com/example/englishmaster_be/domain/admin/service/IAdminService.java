package com.example.englishmaster_be.domain.admin.service;

import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.user.dto.request.UserFilterRequest;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.domain.admin.dto.response.CountMockTestTopicResponse;

import java.util.List;
import java.util.UUID;

public interface IAdminService {


    FilterResponse<?> getAllUser(UserFilterRequest filterRequest);

    void enableUser(UUID userId, Boolean enable);

    void deleteUser(UUID userId);

    List<CountMockTestTopicResponse> getCountMockTestOfTopic(String date, UUID packId);

    List<UserEntity> getUsersNotLoggedInLast10Days();

    List<UserEntity> findUsersInactiveForDaysAndNotify(int inactiveDays);

    List<UserEntity> findUsersInactiveForDays(int inactiveDays);

    void notifyInactiveUsers();

}

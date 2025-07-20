package com.example.englishmaster_be.domain.user.admin.service;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.user.user.dto.view.IUserPageView;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.admin.dto.res.CountMockTestTopicRes;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IAdminService {

    Page<IUserPageView> getPageUser(PageOptionsReq optionsReq);

    void enableUser(UUID userId, Boolean enable);

    void deleteUser(UUID userId);

    List<CountMockTestTopicRes> getCountMockTestOfTopic(String date, UUID packId);

    List<UserEntity> getUsersNotLoggedInLast10Days();

    List<UserEntity> findUsersInactiveForDaysAndNotify(int inactiveDays);

    List<UserEntity> findUsersInactiveForDays(int inactiveDays);

    void notifyInactiveUsers();

}

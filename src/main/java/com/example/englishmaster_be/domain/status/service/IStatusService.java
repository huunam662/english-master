package com.example.englishmaster_be.domain.status.service;

import com.example.englishmaster_be.common.constant.StatusEnum;
import com.example.englishmaster_be.domain.status.dto.request.StatusRequest;
import com.example.englishmaster_be.model.status.StatusEntity;

import java.util.List;
import java.util.UUID;

public interface IStatusService {

    StatusEntity saveStatus(StatusRequest statusRequest);

    List<StatusEntity> getAllStatusByType(UUID typeId);

    boolean isExistedByStatusNameWithDiff(StatusEntity status, StatusEnum statusName);

    boolean isExistedByStatusName(StatusEnum statusName);

    void deleteStatus(UUID statusId);

    StatusEntity getStatusById(UUID statusId);

    StatusEntity getStatusByName(StatusEnum statusName);

}

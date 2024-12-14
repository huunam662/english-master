package com.example.englishmaster_be.service;

import com.example.englishmaster_be.common.constaint.StatusEnum;
import com.example.englishmaster_be.model.request.Status.StatusRequest;
import com.example.englishmaster_be.entity.StatusEntity;

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

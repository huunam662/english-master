package com.example.englishmaster_be.domain.status.service;

import com.example.englishmaster_be.domain.status.dto.request.StatusRequest;
import com.example.englishmaster_be.model.status.StatusEntity;
import com.example.englishmaster_be.model.type.TypeEntity;

import java.util.List;
import java.util.UUID;

public interface IStatusService {

    StatusEntity saveStatus(StatusRequest statusRequest);

    List<StatusEntity> getAllStatusByType(UUID typeId);

    boolean isExistedByStatusNameOfType(String statusName, TypeEntity type);

    void deleteStatus(UUID statusId);

    StatusEntity getStatusById(UUID statusId);

    StatusEntity getStatusByName(String statusName);

}

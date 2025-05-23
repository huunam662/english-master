package com.example.englishmaster_be.domain.status.service;

import com.example.englishmaster_be.domain.status.dto.request.StatusRequest;
import com.example.englishmaster_be.model.status.StatusEntity;

import java.util.List;
import java.util.UUID;

public interface IStatusService {

    StatusEntity saveStatus(StatusRequest statusRequest);

    boolean isExistedByStatusNameOfType(String statusName);

    void deleteStatus(UUID statusId);

    StatusEntity getStatusById(UUID statusId);

    StatusEntity getStatusByName(String statusName);

}

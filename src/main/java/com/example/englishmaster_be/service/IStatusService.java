package com.example.englishmaster_be.service;

import com.example.englishmaster_be.dto.status.CreateStatusDTO;
import com.example.englishmaster_be.dto.status.UpdateStatusDTO;
import com.example.englishmaster_be.model.response.StatusResponse;
import com.example.englishmaster_be.model.Status;

import java.util.List;
import java.util.UUID;

public interface IStatusService {
    StatusResponse createStatus(com.example.englishmaster_be.dto.status.CreateStatusDTO statusDTO);

    List<StatusResponse> getAllStatusByType(UUID typeId);

    void deleteStatus(UUID statusId);

    StatusResponse getStatusById(UUID statusId);

    StatusResponse updateStatus(UpdateStatusDTO statusDTO);
}

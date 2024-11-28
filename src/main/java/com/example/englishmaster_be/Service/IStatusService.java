package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.status.UpdateStatusDTO;
import com.example.englishmaster_be.Model.Response.StatusResponse;

import java.util.List;
import java.util.UUID;

public interface IStatusService {
    StatusResponse createStatus(com.example.englishmaster_be.DTO.status.CreateStatusDTO statusDTO);

    List<StatusResponse> getAllStatusByType(UUID typeId);

    void deleteStatus(UUID statusId);

    StatusResponse getStatusById(UUID statusId);

    StatusResponse updateStatus(UpdateStatusDTO statusDTO);
}

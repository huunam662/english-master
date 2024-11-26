package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Status.CreateStatusDTO;
import com.example.englishmaster_be.DTO.Status.UpdateStatusDTO;
import com.example.englishmaster_be.Model.Response.StatusResponse;
import com.example.englishmaster_be.Model.Status;

import java.util.List;
import java.util.UUID;

public interface IStatusService {
    StatusResponse createStatus(CreateStatusDTO statusDTO);

    List<StatusResponse> getAllStatusByType(UUID typeId);

    void deleteStatus(UUID statusId);

    StatusResponse getStatusById(UUID statusId);

    StatusResponse updateStatus(UpdateStatusDTO statusDTO);
}

package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Status.SaveStatusDTO;
import com.example.englishmaster_be.DTO.Status.UpdateStatusDTO;
import com.example.englishmaster_be.Model.Response.StatusResponse;
import com.example.englishmaster_be.Model.Status;

import java.util.List;
import java.util.UUID;

public interface IStatusService {

    StatusResponse saveStatus(SaveStatusDTO statusDTO);

    List<StatusResponse> getAllStatusByType(UUID typeId);

    void deleteStatus(UUID statusId);

    Status getStatusById(UUID statusId);

    Status getStatusByName(String statusName);

}

package com.example.englishmaster_be.domain.status.contorller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.model.status.StatusEntity;
import com.example.englishmaster_be.mapper.StatusMapper;
import com.example.englishmaster_be.domain.status.dto.request.StatusRequest;
import com.example.englishmaster_be.domain.status.dto.response.StatusResponse;
import com.example.englishmaster_be.domain.status.service.IStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Status")
@RestController
@RequestMapping("/status")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class StatusController {

    IStatusService statusService;

    @PostMapping("/createStatus")
    @DefaultMessage("Create status successfully")
    public StatusResponse createStatus(@RequestBody StatusRequest statusRequest) {

        StatusEntity statusEntity = statusService.saveStatus(statusRequest);

        return StatusMapper.INSTANCE.toStatusResponse(statusEntity);
    }

    @PutMapping("/updateStatus")
    @DefaultMessage("Update status successfully")
    public StatusResponse updateStatus(@RequestBody StatusRequest statusRequest) {

        StatusEntity status = statusService.saveStatus(statusRequest);

        return StatusMapper.INSTANCE.toStatusResponse(status);
    }

    @GetMapping("/getStatusByTypeId/{id}")
    @DefaultMessage("List status successfully")
    public List<StatusResponse> getStatusByTypeId(@PathVariable("id") UUID id) {

        List<StatusEntity> statusEntityList = statusService.getAllStatusByType(id);

        return StatusMapper.INSTANCE.toStatusResponseList(statusEntityList);
    }

    @GetMapping("/getStatusById/{id}")
    @DefaultMessage("Get status successfully")
    public StatusResponse getStatusById(@PathVariable("id") UUID id) {

        StatusEntity status = statusService.getStatusById(id);

        return StatusMapper.INSTANCE.toStatusResponse(status);
    }

    @DeleteMapping("/{statusId}")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete status successfully")
    public void deleteStatusById(@PathVariable("statusId") UUID statusId) {

        statusService.deleteStatus(statusId);
    }
}

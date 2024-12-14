package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Mapper.StatusMapper;
import com.example.englishmaster_be.Model.Request.Status.StatusRequest;
import com.example.englishmaster_be.Model.Response.StatusResponse;
import com.example.englishmaster_be.entity.StatusEntity;
import com.example.englishmaster_be.Service.IStatusService;
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
    @MessageResponse("Create StatusEntity successfully")
    public StatusResponse createStatus(@RequestBody StatusRequest statusRequest) {

        StatusEntity statusEntity = statusService.saveStatus(statusRequest);

        return StatusMapper.INSTANCE.toStatusResponse(statusEntity);
    }

    @PutMapping("/updateStatus")
    @MessageResponse("Update StatusEntity successfully")
    public StatusResponse updateStatus(@RequestBody StatusRequest statusRequest) {

        StatusEntity status = statusService.saveStatus(statusRequest);

        return StatusMapper.INSTANCE.toStatusResponse(status);
    }

    @GetMapping("/getStatusByTypeId/{id}")
    @MessageResponse("List StatusEntity successfully")
    public List<StatusResponse> getStatusByTypeId(@PathVariable("id") UUID id) {

        List<StatusEntity> statusEntityList = statusService.getAllStatusByType(id);

        return StatusMapper.INSTANCE.toStatusResponseList(statusEntityList);
    }

    @GetMapping("/getStatusById/{id}")
    @MessageResponse("Get StatusEntity successfully")
    public StatusResponse getStatusById(@PathVariable("id") UUID id) {

        StatusEntity status = statusService.getStatusById(id);

        return StatusMapper.INSTANCE.toStatusResponse(status);
    }

    @DeleteMapping("/{statusId}")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Delete status successfully")
    public void deleteStatusById(@PathVariable("statusId") UUID statusId) {

        statusService.deleteStatus(statusId);
    }
}

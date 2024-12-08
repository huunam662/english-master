package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.DTO.Status.SaveStatusDTO;
import com.example.englishmaster_be.DTO.Status.UpdateStatusDTO;
import com.example.englishmaster_be.Model.Response.StatusResponse;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Model.Status;
import com.example.englishmaster_be.Service.IStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Status")
@RestController
@RequestMapping("/api/status")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class StatusController {

    IStatusService statusService;

    @PostMapping("/createStatus")
    @MessageResponse("Create Status successfully")
    public StatusResponse createStatus(@RequestBody SaveStatusDTO createStatusDTO) {

        return statusService.saveStatus(createStatusDTO);
    }

    @GetMapping("/getStatusByTypeId/{id}")
    @MessageResponse("List Status successfully")
    public List<StatusResponse> getStatusByTypeId(@PathVariable("id") UUID id) {

        return statusService.getAllStatusByType(id);
    }

    @GetMapping("/getStatusById/{id}")
    @MessageResponse("Get Status successfully")
    public StatusResponse getStatusById(@PathVariable("id") UUID id) {

        Status status = statusService.getStatusById(id);

        return new StatusResponse(status);
    }

    @PutMapping("/updateStatus")
    @MessageResponse("Update Status successfully")
    public StatusResponse updateStatus(@RequestBody UpdateStatusDTO updateStatusDTO) {

        return statusService.saveStatus(updateStatusDTO);
    }
}

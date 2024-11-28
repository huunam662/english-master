package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.DTO.status.CreateStatusDTO;
import com.example.englishmaster_be.DTO.status.UpdateStatusDTO;
import com.example.englishmaster_be.Model.Response.StatusResponse;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.IStatusService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/status")
@PreAuthorize("hasRole('ADMIN')")
public class StatusController {
    @Autowired
    IStatusService statusService;

    @PostMapping("/createStatus")
    public ResponseEntity<ResponseModel> createStatus(@RequestBody CreateStatusDTO createStatusDTO) {
        ResponseModel responseModel = new ResponseModel();
        StatusResponse statusResponse = statusService.createStatus(createStatusDTO);
        if (statusResponse == null) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create status failed");
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("status", statusResponse);

        responseModel.setMessage("Create status successful");
        responseModel.setResponseData(responseObject);


        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @GetMapping("/getStatusByTypeId/{id}")
    public ResponseEntity<ResponseModel> getStatusByTypeId(@PathVariable("id") UUID id) {
        ResponseModel responseModel = new ResponseModel();
        List<StatusResponse> statusResponse = statusService.getAllStatusByType(id);
        if (statusResponse.isEmpty()) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("No status found");
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("statuses", statusResponse);

        responseModel.setMessage("List status successful");
        responseModel.setResponseData(responseObject);


        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @GetMapping("/getStatusById/{id}")
    public ResponseEntity<ResponseModel> getStatusById(@PathVariable("id") UUID id) {
        ResponseModel responseModel = new ResponseModel();
        StatusResponse statusResponse = statusService.getStatusById(id);
        if (statusResponse == null) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("No status found");
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("statuses", statusResponse);

        responseModel.setMessage("status successful");
        responseModel.setResponseData(responseObject);


        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<ResponseModel> updateStatus(@RequestBody UpdateStatusDTO updateStatusDTO) {
        ResponseModel responseModel = new ResponseModel();
        StatusResponse statusResponse = statusService.updateStatus(updateStatusDTO);
        if (statusResponse == null) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Update status failed");
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("status", statusResponse);
        responseModel.setMessage("Update status successful");
        responseModel.setResponseData(responseObject);

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }
}

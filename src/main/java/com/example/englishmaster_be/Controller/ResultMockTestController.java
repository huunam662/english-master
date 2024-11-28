package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.DTO.mockTest.CreateResultMockTestDTO;
import com.example.englishmaster_be.Model.Response.ResultMockTestResponse;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.IResultMockTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('ADMIN')||hasRole('USER')")
public class ResultMockTestController {
    @Autowired
    private IResultMockTestService IResultMockTestService;

    @PostMapping(value = "/create")
    public ResponseEntity<ResponseModel> createResultMockTest(CreateResultMockTestDTO CreateResultMockTestDTO) {
        ResponseModel responseModel = new ResponseModel();

        ResultMockTestResponse resultMockTestResponse = IResultMockTestService.createResultMockTest(CreateResultMockTestDTO);

        if (resultMockTestResponse != null) {
            responseModel.setResponseData(resultMockTestResponse);

            responseModel.setMessage("Create result mock test successfully");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } else {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setMessage("Create result mock test failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping("/getAllResult")
    public ResponseEntity<ResponseModel> getAllResult() {
        ResponseModel responseModel = new ResponseModel();
        List<ResultMockTestResponse> resultMockTestResponses = IResultMockTestService.getAllResultMockTests();
        if (resultMockTestResponses != null && !resultMockTestResponses.isEmpty()) {
            responseModel.setResponseData(resultMockTestResponses);

            responseModel.setMessage("Get all result mock test successfully");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } else {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setMessage("Get all result mock test failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping("/getResultMockTestByPartAndMockTest")
    public ResponseEntity<ResponseModel> getResultMockTest(@RequestParam(required = false) UUID partId, @RequestParam(required = false) UUID mockTestId) {
        ResponseModel responseModel = new ResponseModel();
        List<ResultMockTestResponse> resultMockTestResponses = IResultMockTestService.getResultMockTestsByPartIdAndMockTestId(partId, mockTestId);
        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        if ((resultMockTestResponses != null) && !resultMockTestResponses.isEmpty()) {
            responseModel.setResponseData(resultMockTestResponses);

            responseModel.setMessage("Get result mock test successfully");
        } else {
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setMessage("Get result mock test failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseModel> deleteResultMockTest(@RequestParam UUID uuid) {
        ResponseModel responseModel = new ResponseModel();
        IResultMockTestService.deleteResultMockTestById(uuid);

        responseModel.setMessage("Delete result mock test successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);

    }
}

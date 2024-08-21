package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.MockTest.CreateResultMockTestDTO;
import com.example.englishmaster_be.Model.Response.ResultMockTestResponse;
import com.example.englishmaster_be.Model.ResponseModel;
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
            responseModel.setStatus("success");
            responseModel.setMessage("Create result mock test successfully");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } else {
            responseModel.setStatus("fail");
            responseModel.setMessage("Create result mock test failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping("/getAllResult")
    public ResponseEntity<ResponseModel> getAllResult() {
        ResponseModel responseModel = new ResponseModel();
        List<ResultMockTestResponse> resultMockTestResponses = IResultMockTestService.getAllResultMockTests();
        if (resultMockTestResponses != null) {
            responseModel.setResponseData(resultMockTestResponses);
            responseModel.setStatus("success");
            responseModel.setMessage("Get all result mock test successfully");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } else {
            responseModel.setStatus("fail");
            responseModel.setMessage("Get all result mock test failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @GetMapping("/getResultMockTestByPartAndMockTest")
    public ResponseEntity<ResponseModel> getResultMockTest(@RequestParam(required = false) UUID partId, @RequestParam(required = false) UUID mockTestId) {
        ResponseModel responseModel = new ResponseModel();
        List<ResultMockTestResponse> resultMockTestResponses = IResultMockTestService.getResultMockTestsByPartIdAndMockTestId(partId, mockTestId);

        if (resultMockTestResponses != null) {
            responseModel.setResponseData(resultMockTestResponses);
            responseModel.setStatus("success");
            responseModel.setMessage("Get result mock test successfully");
        } else {
            responseModel.setStatus("fail");
            responseModel.setMessage("Get result mock test failed");
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseModel> deleteResultMockTest(@RequestParam UUID uuid) {
        ResponseModel responseModel = new ResponseModel();
        IResultMockTestService.deleteResultMockTestById(uuid);

        responseModel.setStatus("success");
        responseModel.setMessage("Delete result mock test successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);

    }
}

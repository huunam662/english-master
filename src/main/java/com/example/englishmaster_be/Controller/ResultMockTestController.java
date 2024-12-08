package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.DTO.MockTest.SaveResultMockTestDTO;
import com.example.englishmaster_be.Model.Response.ResultMockTestResponse;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.IResultMockTestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Result Mock Test")
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('ADMIN')||hasRole('USER')")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResultMockTestController {

    IResultMockTestService resultMockTestService;

    @PostMapping(value = "/create")
    @MessageResponse("Create result mock test successfully")
    public ResultMockTestResponse createResultMockTest(SaveResultMockTestDTO saveResultMockTestDTO) {

        return resultMockTestService.createResultMockTest(saveResultMockTestDTO);
    }

    @GetMapping("/getAllResult")
    @MessageResponse("Get all result mock test successfully")
    public List<ResultMockTestResponse> getAllResult() {

        return resultMockTestService.getAllResultMockTests();
    }

    @GetMapping("/getResultMockTestByPartAndMockTest")
    @MessageResponse("Get result mock test successfully")
    public List<ResultMockTestResponse> getResultMockTest(@RequestParam(required = false) UUID partId, @RequestParam(required = false) UUID mockTestId) {

        return resultMockTestService.getResultMockTestsByPartIdAndMockTestId(partId, mockTestId);
    }

    @DeleteMapping("/delete")
    @MessageResponse("Delete result mock test successfully")
    public void deleteResultMockTest(@RequestParam UUID uuid) {

        resultMockTestService.deleteResultMockTestById(uuid);
    }
}

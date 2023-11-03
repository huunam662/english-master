package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Helper.ExcelHelper;
import com.example.englishmaster_be.Model.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseModel> uploadFile(@ModelAttribute MultipartFile file) throws IOException {
        ResponseModel responseModel = new ResponseModel();

        if (ExcelHelper.hasExcelFormat(file)) {
//            try {
                ExcelHelper.excelToQuestionPart5(file.getInputStream());

                responseModel.setResponseData("Uploaded the file successfully: " + file.getOriginalFilename());
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
//            } catch (Exception e) {
//                responseModel.setResponseData("Could not upload the file: " + file.getOriginalFilename() + "!");
//                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
//            }
        }
        responseModel.setResponseData("Please upload an excel file!");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseModel);
    }
}

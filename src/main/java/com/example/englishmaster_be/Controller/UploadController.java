package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.DeleteRequestDto;
import com.example.englishmaster_be.Model.Response.DeleteResponse;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Service.IUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    @Autowired
    private IUploadService IUploadService;

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dir", defaultValue = "/") String dir,
            @RequestParam(value = "isPrivateFile", defaultValue = "false") boolean isPrivateFile,
            @RequestParam(value = "topicId", required = false) UUID topicId,
            @RequestParam(value = "code", required = false) String code) {
        ResponseModel responseModel = new ResponseModel();
        String urlFile = IUploadService.upload(file, dir, isPrivateFile, topicId, code);
        if (urlFile != null && urlFile.startsWith("https")) {
            responseModel.setMessage("Successfully uploaded file");
            responseModel.setStatus("success");
            responseModel.setResponseData(urlFile);
            return ResponseEntity.ok(responseModel);
        } else {
            responseModel.setMessage("Upload failed");
            responseModel.setStatus("failed");
            responseModel.setResponseData(null);
            return ResponseEntity.badRequest().body(responseModel);
        }
    }

    @DeleteMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseModel> deleteFile(@RequestBody DeleteRequestDto dto) throws FileNotFoundException {
        ResponseModel responseModel = new ResponseModel();
        DeleteResponse response = IUploadService.delete(dto);
        if (response.getMessage().equalsIgnoreCase("Image deleted")) {
            responseModel.setMessage("Successfully uploaded file");
            responseModel.setStatus("success");
            responseModel.setResponseData(response.getMessage());
            return ResponseEntity.ok(responseModel);
        } else {
            responseModel.setMessage("Deleted failed");
            responseModel.setStatus("failed");
            responseModel.setResponseData(null);
            return ResponseEntity.badRequest().body(responseModel);
        }
    }

}

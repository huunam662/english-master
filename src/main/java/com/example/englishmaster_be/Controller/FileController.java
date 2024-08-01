package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Service.IFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private IFileStorageService IFileStorageService;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = IFileStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/showImage/{filename:.+}")
    public ResponseEntity<?> showImage(@PathVariable String filename) {
        Resource file = IFileStorageService.load(filename);
        if (file == null) {
            ResponseModel responseModel = new ResponseModel();
            responseModel.setStatus("fail");
            responseModel.setMessage("Image not found");
            responseModel.setViolations("404");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseModel);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG).body(file);
    }


    @GetMapping("/showAudio/{filename:.+}")
    public ResponseEntity<Resource> showAudio(@PathVariable String filename) {
        Resource file = IFileStorageService.load(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/mpeg")).body(file);
    }

    @GetMapping("/getImageName")
    public ResponseEntity<List<String>> showImages() {
        List<String> files = IFileStorageService.loadAll();
        return ResponseEntity.ok().body(files);
    }

    @PostMapping(value = "/saveImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel> saveImage(@RequestParam("file") MultipartFile file) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String fileName = IFileStorageService.nameFile(file);
            IFileStorageService.save(file, fileName);
            responseModel.setStatus("success");
            responseModel.setMessage("Save file sucessfully");
            responseModel.setResponseData(fileName);
            return ResponseEntity.ok().body(responseModel);
        } catch (Exception e) {
            responseModel.setStatus("failed");
            responseModel.setMessage("Save file failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/deleteImage/{fileName}")
    public ResponseEntity<ResponseModel> deleteImage(@PathVariable("fileName") String fileName) {
        ResponseModel responseModel = new ResponseModel();
        try {
            IFileStorageService.delete(fileName);
            responseModel.setStatus("success");
            responseModel.setMessage("Delete file sucessfully");
            responseModel.setResponseData(fileName);
            return ResponseEntity.ok().body(responseModel);
        } catch (Exception e) {
            responseModel.setStatus("failed");
            responseModel.setMessage("Delete file failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

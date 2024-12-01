package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.FileResponse;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.IFileStorageService;
import com.example.englishmaster_be.Util.ServletUtil;
import com.google.cloud.storage.Blob;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {

    IFileStorageService fileStorageService;

    @GetMapping("/{filename:.+}")
    @MessageResponse("Load file successfully")
    public Resource getFile(@PathVariable String filename) {

        Resource file = fileStorageService.load(filename);

        HttpServletResponse httpServletResponse = ServletUtil.getResponse();

        httpServletResponse.setHeader(
                HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\""
        );

        return file;
    }

    @GetMapping("/showImage/{filename:.+}")
    @MessageResponse("Load file successfully")
    public Resource showImage(@PathVariable String filename) {

        Resource file = fileStorageService.load(filename);

        HttpServletResponse httpServletResponse = ServletUtil.getResponse();

        httpServletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);

        return file;
    }


    @GetMapping("/showAudio/{filename:.+}")
    @MessageResponse("Load file successfully")
    public Resource showAudio(@PathVariable String filename) {

        Resource file = fileStorageService.load(filename);

        HttpServletResponse httpServletResponse = ServletUtil.getResponse();

        httpServletResponse.setContentType(MediaType.valueOf("audio/mpeg").toString());

        return file;
    }

    @GetMapping("/getImageName")
    @MessageResponse("Load file names successfully")
    public List<String> showImages() {

        return fileStorageService.loadAll();
    }

    @PostMapping(value = "/saveImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MessageResponse("Save file successfully")
    public FileResponse saveImage(@RequestParam("file") MultipartFile file) {

        Blob blob = fileStorageService.save(file);

        return FileResponse.builder()
                .fileName(blob.getName())
                .build();
    }

    @DeleteMapping("/deleteImage/{fileName}")
    @MessageResponse("Delete file successfully")
    public void deleteImage(@PathVariable("fileName") String fileName) {

        fileStorageService.delete(fileName);
    }
}

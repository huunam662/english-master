package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Response.FileResponse;
import com.example.englishmaster_be.Service.IFileStorageService;
import com.google.cloud.storage.Blob;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {

    IFileStorageService fileStorageService;

    @GetMapping("/{filename:.+}")
    @MessageResponse("Load file successfully")
    public Resource getFile(
            @Parameter(hidden = true) HttpServletResponse response,
            @PathVariable String filename
    ) {

        Resource file = fileStorageService.load(filename);

        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\""
        );

        return file;
    }

    @GetMapping("/showImage/{filename:.+}")
    @MessageResponse("Load file successfully")
    public Resource showImage(
            @Parameter(hidden = true) HttpServletResponse response,
            @PathVariable String filename
    ) {

        Resource file = fileStorageService.load(filename);

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        return file;
    }


    @GetMapping("/showAudio/{filename:.+}")
    @MessageResponse("Load file successfully")
    public Resource showAudio(
            @Parameter(hidden = true) HttpServletResponse response,
            @PathVariable String filename
    ) {

        Resource file = fileStorageService.load(filename);

        response.setContentType(MediaType.valueOf("audio/mpeg").toString());

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

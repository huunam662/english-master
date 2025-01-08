package com.example.englishmaster_be.domain.file_storage.controller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.common.constant.ResourceTypeLoadEnum;
import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import com.example.englishmaster_be.domain.file_storage.dto.response.ResourceResponse;
import com.example.englishmaster_be.domain.file_storage.service.IFileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File Storage")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileStorageController {

    IFileStorageService fileStorageService;

    @GetMapping("/download")
    @DefaultMessage("Load file successfully")
    public ResourceResponse getFile(
            @RequestParam("fileName") String fileName
    ) {

        ResourceResponse resourceResponse = fileStorageService.load(fileName);

        resourceResponse.setTypeLoad(ResourceTypeLoadEnum.ATTACHMENT); // Tải tập tin

        return resourceResponse;
    }

    @GetMapping("/show/{fileName:.+}")
    @DefaultMessage("Load file successfully")
    public ResourceResponse showImage(
            @PathVariable String fileName
    ) {

        ResourceResponse resourceResponse = fileStorageService.load(fileName);

        resourceResponse.setTypeLoad(ResourceTypeLoadEnum.INLINE); // Xem tập tin

        return resourceResponse;
    }


//    @GetMapping("/showAudio/{filename:.+}")
//    @DefaultMessage("Load file successfully")
//    public ResourceResponse showAudio(
//            @PathVariable String filename
//    ) {
//
//        return fileStorageService.load(filename);
//    }

    @GetMapping("/list-file-name")
    @DefaultMessage("List file name successfully")
    public List<String> showImages() {

        return fileStorageService.loadAll();
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("Save file successfully")
    public FileResponse saveImage(@RequestPart("file") MultipartFile file) {

        return fileStorageService.save(file);
    }

    @DeleteMapping("/delete")
    @DefaultMessage("Delete file successfully")
    public void deleteImage(@RequestParam("fileName") String fileName) {

        fileStorageService.delete(fileName);
    }
}

package com.example.englishmaster_be.domain.cloudinary.controller;


import com.example.englishmaster_be.domain.cloudinary.service.ICloudinaryService;
import com.example.englishmaster_be.shared.dto.response.FileResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Cloudinary")
@RestController
@RequestMapping("/cloudinary/upload")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryController {

	ICloudinaryService cloudinaryService;


	@PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileResponse uploadImage(@RequestPart("image") MultipartFile file) {

		return cloudinaryService.uploadFile(file);
	}

	@PostMapping(value = "/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileResponse uploadAudio(@RequestPart("audio") MultipartFile file) {

		return cloudinaryService.uploadAudio(file);
	}
}

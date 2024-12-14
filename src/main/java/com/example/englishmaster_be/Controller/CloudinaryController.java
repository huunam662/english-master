package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Response.CloudiaryUploadFileResponse;
import com.example.englishmaster_be.Service.ICloudinaryService;
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
	@MessageResponse("File uploaded successfully")
	public CloudiaryUploadFileResponse uploadImage(@RequestParam("image") MultipartFile file) {

		return cloudinaryService.uploadFile(file);
	}
}

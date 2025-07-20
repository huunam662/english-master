package com.example.englishmaster_be.domain.upload.cloudinary.controller;


import com.example.englishmaster_be.domain.upload.cloudinary.dto.req.CloudinaryOptionsReq;
import com.example.englishmaster_be.domain.upload.cloudinary.dto.res.CloudinaryFileRes;
import com.example.englishmaster_be.domain.upload.cloudinary.dto.res.CloudinaryPageFileRes;
import com.example.englishmaster_be.domain.upload.cloudinary.service.ICloudinaryService;
import com.example.englishmaster_be.common.dto.res.FileRes;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

@Tag(name = "Cloudinary")
@RestController
@RequestMapping("/cloudinary")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryController {

	ICloudinaryService cloudinaryService;

	@PostMapping(value = "/upload/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileRes uploadImage(@RequestPart("image") MultipartFile file) {
		return cloudinaryService.uploadFile(file);
	}

	@PostMapping(value = "/upload/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileRes uploadAudio(@RequestPart("audio") MultipartFile file) {
		return cloudinaryService.uploadAudio(file);
	}

	@GetMapping("/show")
	public byte[] show(@RequestParam("publicId") String publicId, HttpServletResponse response) {
		return cloudinaryService.getFileByteToPublicId(publicId, null, response);
	}

	@GetMapping("/download")
	public byte[] download(@RequestParam("publicId") String publicId, HttpServletResponse response) {
		return cloudinaryService.getFileByteToPublicId(publicId, "download", response);
	}

	@GetMapping
	public List<CloudinaryFileRes> getFileList() throws Exception {
		return cloudinaryService.getFileList();
	}

	@GetMapping("/{publicId}")
	public CloudinaryFileRes getSingleFile(@PathVariable String publicId) throws Exception {
		return cloudinaryService.getFileToPublicId(publicId);
	}

	@GetMapping("/page")
	public CloudinaryPageFileRes getPageFile(@ModelAttribute @Valid CloudinaryOptionsReq params) throws Exception {
		return cloudinaryService.getPageFile(params);
	}

	@DeleteMapping("/{publicId}")
	public void deleteSingleFile(@PathVariable("publicId") String publicId) throws FileNotFoundException, BadRequestException {
		cloudinaryService.deleteFileByPublicId(publicId);
	}
}

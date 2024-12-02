package com.example.englishmaster_be.Service.impl;

import com.cloudinary.Cloudinary;
import com.example.englishmaster_be.Model.Response.CloudiaryUploadFileResponse;
import com.example.englishmaster_be.Service.ICloudinaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryServiceimpl implements ICloudinaryService {

	Cloudinary cloudinary;

	@SneakyThrows
	public CloudiaryUploadFileResponse uploadFile(MultipartFile file){

			Map<String, Object> uploadResultResponse = cloudinary.uploader().upload(file.getBytes(), Map.of());

			String imageUrl = String.valueOf(uploadResultResponse.get("url"));
			String fileType = String.valueOf(uploadResultResponse.get("type"));

			return CloudiaryUploadFileResponse.builder()
					.url(imageUrl)
					.type(fileType)
					.build();
	}


}

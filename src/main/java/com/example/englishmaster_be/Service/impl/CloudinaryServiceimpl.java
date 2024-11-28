package com.example.englishmaster_be.Service.impl;

import com.cloudinary.Cloudinary;
import com.example.englishmaster_be.Model.Response.CloudiaryUploadFileResponse;
import com.example.englishmaster_be.Service.ICloudinaryService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryServiceimpl implements ICloudinaryService {

	private final Cloudinary cloudinary;

	public CloudinaryServiceimpl(Cloudinary cloudinary) {
		this.cloudinary = cloudinary;
	}

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

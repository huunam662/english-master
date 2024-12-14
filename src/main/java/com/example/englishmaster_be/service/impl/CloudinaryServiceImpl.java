package com.example.englishmaster_be.service.impl;

import com.cloudinary.Cloudinary;
import com.example.englishmaster_be.model.response.CloudiaryUploadFileResponse;
import com.example.englishmaster_be.service.ICloudinaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryServiceImpl implements ICloudinaryService {

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

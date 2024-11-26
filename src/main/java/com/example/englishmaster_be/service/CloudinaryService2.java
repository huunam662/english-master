package com.example.englishmaster_be.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService2 {

	private final Cloudinary cloudinary;

	public CloudinaryService2(Cloudinary cloudinary) {
		this.cloudinary = cloudinary;
	}

	public Map uploadFile(MultipartFile file) throws IOException {
		return cloudinary.uploader().upload(file.getBytes(), Map.of());
	}


}

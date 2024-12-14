package com.example.englishmaster_be.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.englishmaster_be.value.CloudinaryValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CloudinaryConfig {

	CloudinaryValue cloudinaryValue;

	@Bean
	public Cloudinary cloudinary() {
		return new Cloudinary(ObjectUtils.asMap(
				"cloud_name", cloudinaryValue.getCloudName(),
				"api_key", cloudinaryValue.getApiKey(),
				"api_secret", cloudinaryValue.getApiSecret()
		));
	}
}

package com.example.englishmaster_be.config.app.cloud;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.englishmaster_be.value.CloudinaryValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

	private final CloudinaryValue cloudinaryValue;

	public CloudinaryConfig(CloudinaryValue cloudinaryValue) {
		this.cloudinaryValue = cloudinaryValue;
	}

	@Bean
	public Cloudinary cloudinary() {
		return new Cloudinary(ObjectUtils.asMap(
				"cloud_name", cloudinaryValue.getCloudName(),
				"api_key", cloudinaryValue.getApiKey(),
				"api_secret", cloudinaryValue.getApiSecret()
		));
	}
}

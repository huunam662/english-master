package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.CloudinaryService2;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/cloudinary/upload")
@RequiredArgsConstructor
public class CloudinaryController {
	@Autowired
	public CloudinaryService2 cloudinaryService;


	@PostMapping(value = "/images", consumes = "multipart/form-data")
	public ResponseEntity<ResponseModel> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
		ResponseModel responseModel = new ResponseModel(); // Khởi tạo trong mỗi request

		try {
			// Upload file lên Cloudinary và lấy kết quả
			Map<String, Object> data = cloudinaryService.uploadFile(file);

			// Lấy url và Type từ kết quả
			String imageUrl = (String) data.get("url");
			String fileType = (String) data.get("type");

			// Tạo một Map chỉ chứa url và Type
			Map<String, Object> responseData = new HashMap<>();
			responseData.put("url", imageUrl);
			responseData.put("type", fileType);

			// Thiết lập message, responseData và Status
			responseModel.setMessage("File uploaded successfully");
			responseModel.setResponseData(responseData);

			// Trả về Response
			return ResponseEntity.ok(responseModel);
		} catch (Exception e) {
			ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
			// Thiết lập message và Status cho lỗi chung
			errorResponseModel.setMessage("File upload failed: " + e.getMessage());
			errorResponseModel.setResponseData(null);
			errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

			// Trả về Response với trạng thái lỗi chung
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
		}
	}
}

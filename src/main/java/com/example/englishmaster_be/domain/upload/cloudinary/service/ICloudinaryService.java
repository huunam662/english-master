package com.example.englishmaster_be.domain.upload.cloudinary.service;

import com.example.englishmaster_be.domain.upload.cloudinary.dto.request.CloudinaryOptionsRequest;
import com.example.englishmaster_be.domain.upload.cloudinary.dto.response.CloudinaryFileResponse;
import com.example.englishmaster_be.domain.upload.cloudinary.dto.response.CloudinaryPageFileResponse;
import com.example.englishmaster_be.common.dto.response.FileResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

public interface ICloudinaryService {

    FileResponse uploadFile(MultipartFile file);

    FileResponse uploadAudio(MultipartFile file);

    List<CloudinaryFileResponse> getFileList() throws Exception;

    CloudinaryPageFileResponse getPageFile(CloudinaryOptionsRequest params) throws Exception;

    CloudinaryFileResponse getFileToPublicId(String publicId) throws FileNotFoundException;

    byte[] getFileByteToPublicId(String publicId, String type, HttpServletResponse response);

    void deleteFileByPublicId(String publicId) throws FileNotFoundException, BadRequestException;
}

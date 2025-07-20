package com.example.englishmaster_be.domain.upload.cloudinary.service;

import com.example.englishmaster_be.domain.upload.cloudinary.dto.req.CloudinaryOptionsReq;
import com.example.englishmaster_be.domain.upload.cloudinary.dto.res.CloudinaryFileRes;
import com.example.englishmaster_be.domain.upload.cloudinary.dto.res.CloudinaryPageFileRes;
import com.example.englishmaster_be.common.dto.res.FileRes;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

public interface ICloudinaryService {

    FileRes uploadFile(MultipartFile file);

    FileRes uploadAudio(MultipartFile file);

    List<CloudinaryFileRes> getFileList() throws Exception;

    CloudinaryPageFileRes getPageFile(CloudinaryOptionsReq params) throws Exception;

    CloudinaryFileRes getFileToPublicId(String publicId) throws FileNotFoundException;

    byte[] getFileByteToPublicId(String publicId, String type, HttpServletResponse response);

    void deleteFileByPublicId(String publicId) throws FileNotFoundException, BadRequestException;
}

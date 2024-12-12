package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Request.DeleteRequestDTO;
import com.example.englishmaster_be.Model.Response.DeleteResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.UUID;

public interface IUploadService {

    String upload(MultipartFile file, String dir, boolean isPrivateFile, UUID topicId, String code);

    DeleteResponse delete(DeleteRequestDTO dto) throws FileNotFoundException;

}

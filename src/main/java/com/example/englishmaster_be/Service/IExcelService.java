package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Response.excel.CreateListQuestionByExcelFileResponse;
import com.example.englishmaster_be.Model.Response.excel.CreateTopicByExcelFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface IExcelService {

    CreateTopicByExcelFileResponse parseCreateTopicDTO(MultipartFile file) throws IOException;

    CreateListQuestionByExcelFileResponse parseReadingPart67DTO(UUID topicId, MultipartFile file, int part) throws IOException;

    CreateListQuestionByExcelFileResponse parseReadingPart5DTO(UUID topicId, MultipartFile file) throws IOException;

    CreateListQuestionByExcelFileResponse parseListeningPart12DTO(UUID topicId, MultipartFile file, int part) throws IOException;

    CreateListQuestionByExcelFileResponse parseListeningPart34DTO(UUID topicId, MultipartFile file, int part) throws IOException;

    CreateListQuestionByExcelFileResponse parseAllPartsDTO(UUID topicId, MultipartFile file) throws IOException;

}

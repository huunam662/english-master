package com.example.englishmaster_be.domain.part.service;

import com.example.englishmaster_be.domain.part.dto.request.PartRequest;
import com.example.englishmaster_be.shared.upload_file.dto.request.UploadMultipleFileRequest;
import com.example.englishmaster_be.domain.part.dto.request.PartSaveContentRequest;
import com.example.englishmaster_be.model.part.PartEntity;

import java.util.List;
import java.util.UUID;

public interface IPartService {

    PartEntity savePart(PartRequest partRequest);

    PartEntity getPartToId(UUID partId);

    PartEntity getPartToName(String partName);

    PartEntity uploadFilePart(UUID partId, UploadMultipleFileRequest uploadMultiFileRequest);

    PartEntity uploadTextPart(UUID partId, PartSaveContentRequest uploadTextRequest);

    List<PartEntity> getListPart();

    boolean isExistedPartNameWithDiff(PartEntity part, String partName);

    boolean isExistedPartName(String partName);

    void deletePart(UUID partId);

}

package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.request.Part.PartRequest;
import com.example.englishmaster_be.model.request.UploadMultiFileRequest;
import com.example.englishmaster_be.model.request.UploadTextRequest;
import com.example.englishmaster_be.entity.PartEntity;

import java.util.List;
import java.util.UUID;

public interface IPartService {

    PartEntity savePart(PartRequest partRequest);

    PartEntity getPartToId(UUID partId);

    PartEntity getPartToName(String partName);

    PartEntity uploadFilePart(UUID partId, UploadMultiFileRequest uploadMultiFileRequest);

    PartEntity uploadTextPart(UUID partId, UploadTextRequest uploadTextRequest);

    List<PartEntity> getListPart();

    boolean isExistedPartNameWithDiff(PartEntity part, String partName);

    boolean isExistedPartName(String partName);

    void deletePart(UUID partId);

}

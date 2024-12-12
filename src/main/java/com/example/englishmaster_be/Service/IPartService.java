package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Request.Part.PartRequest;
import com.example.englishmaster_be.Model.Request.UploadMultiFileRequest;
import com.example.englishmaster_be.Model.Request.UploadTextRequest;
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

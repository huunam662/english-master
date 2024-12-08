package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Part.SavePartDTO;
import com.example.englishmaster_be.DTO.UploadMultiFileDTO;
import com.example.englishmaster_be.DTO.UploadTextDTO;
import com.example.englishmaster_be.Model.Part;
import com.example.englishmaster_be.Model.Response.PackResponse;
import com.example.englishmaster_be.Model.Response.PartResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IPartService {

    PartResponse savePart(SavePartDTO savePartDTO);

    List<PartResponse> getListPart();

    Part getPartToId(UUID partId);

    Part getPartToName(String partName);

    boolean notExistedPart(Part part);

    void deletePart(UUID partId);

    PartResponse uploadFilePart(UUID partId, UploadMultiFileDTO uploadMultiFileDTO);

    PartResponse uploadTextPart(UUID partId, UploadTextDTO uploadTextDTO);

}

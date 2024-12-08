package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Part.SavePartDTO;
import com.example.englishmaster_be.DTO.Part.UpdatePartDTO;
import com.example.englishmaster_be.DTO.UploadMultiFileDTO;
import com.example.englishmaster_be.DTO.UploadTextDTO;
import com.example.englishmaster_be.Exception.CustomException;
import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Mapper.PartMapper;
import com.example.englishmaster_be.Model.Part;
import com.example.englishmaster_be.Model.Response.PartResponse;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IFileStorageService;
import com.example.englishmaster_be.Service.IPartService;
import com.example.englishmaster_be.Service.IUserService;
import com.google.cloud.storage.Blob;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartServiceImpl implements IPartService {

    PartRepository partRepository;

    IUserService userService;

    IFileStorageService fileStorageService;


    @Transactional
    @Override
    public PartResponse savePart(SavePartDTO savePartDTO) {

        Part partEntity;

        String messageBadRequestException = "Create Part fail: The Part name is already exist";

        if(savePartDTO instanceof UpdatePartDTO updatePartDTO){

            partEntity = getPartToId(updatePartDTO.getPartId());

            if(
                    !partEntity.getPartName().equalsIgnoreCase(updatePartDTO.getPartName())
                    && notExistedPart(partEntity)
            ) throw new BadRequestException(messageBadRequestException);
        }
        else{

            partEntity = PartMapper.INSTANCE.savePartDtoToPartEntity(savePartDTO);

            if(notExistedPart(partEntity))
                throw new BadRequestException(messageBadRequestException);

            partEntity.setCreateAt(LocalDateTime.now());
        }

        User user = userService.currentUser();

        partEntity = PartMapper.INSTANCE.savePartDtoToPartEntity(savePartDTO);

        partEntity.setPartName(partEntity.getPartName().toUpperCase());
        partEntity.setPartType(partEntity.getPartType().toUpperCase());
        partEntity.setUserCreate(user);
        partEntity.setUserUpdate(user);
        partEntity.setUpdateAt(LocalDateTime.now());

        partEntity = partRepository.save(partEntity);

        return PartMapper.INSTANCE.partEntityToPartResponse(partEntity);
    }


    @Override
    public List<PartResponse> getListPart() {

        List<Part> partList = partRepository.findAll();

        return PartMapper.INSTANCE.partEntityListToPartResponseList(partList);
    }

    @Override
    public Part getPartToId(UUID partId) {
        return partRepository.findByPartId(partId)
                .orElseThrow(
                        () -> new CustomException(Error.PART_NOT_FOUND)
                );
    }

    @Override
    public Part getPartToName(String partName) {

        List<Part> listPart = partRepository.findAll();

        return listPart.stream().filter(
                part -> part.getPartName().substring(0, 6).equalsIgnoreCase(partName)
        ).findFirst().orElse(null);
    }

    @Override
    public boolean notExistedPart(Part part) {

        List<Part> partList = partRepository.findAll();

        for (Part partCheck : partList)
            if (partCheck.getPartName().equalsIgnoreCase(part.getPartName()))
                return true;

        return false;
    }


    @Override
    public void deletePart(UUID partId) {

        Part partEntity = getPartToId(partId);

        partRepository.delete(partEntity);
    }


    @Transactional
    @Override
    public PartResponse uploadFilePart(UUID partId, UploadMultiFileDTO uploadMultiFileDTO) {

        User user = userService.currentUser();

        Part partEntity = getPartToId(partId);

        for(MultipartFile file : uploadMultiFileDTO.getContentData()){

            if(file == null || file.isEmpty()) continue;

            if (partEntity.getContentType() != null && !partEntity.getContentType().isEmpty())
                fileStorageService.delete(partEntity.getContentData());

            Blob blobResponse = fileStorageService.save(file);

            partEntity.setContentType(GetExtension.typeFile(blobResponse.getName()));
            partEntity.setContentData(blobResponse.getName());
            partEntity.setUserUpdate(user);
            partEntity.setUpdateAt(LocalDateTime.now());
        }

        partEntity = partRepository.save(partEntity);

        return PartMapper.INSTANCE.partEntityToPartResponse(partEntity);
    }


    @Transactional
    @Override
    public PartResponse uploadTextPart(UUID partId, UploadTextDTO uploadTextDTO) {

        User user = userService.currentUser();

        Part partEntity = getPartToId(partId);

        partEntity.setContentData(uploadTextDTO.getContentData());
        partEntity.setContentType(uploadTextDTO.getContentType());
        partEntity.setUserUpdate(user);
        partEntity.setUpdateAt(LocalDateTime.now());

        partEntity = partRepository.save(partEntity);

        return PartMapper.INSTANCE.partEntityToPartResponse(partEntity);
    }
}

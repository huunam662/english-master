package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Common.enums.ErrorEnum;
import com.example.englishmaster_be.Model.Request.Part.PartRequest;
import com.example.englishmaster_be.Model.Request.UploadMultiFileRequest;
import com.example.englishmaster_be.Model.Request.UploadTextRequest;
import com.example.englishmaster_be.Exception.template.CustomException;
import com.example.englishmaster_be.Exception.template.BadRequestException;
import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Mapper.PartMapper;
import com.example.englishmaster_be.entity.PartEntity;
import com.example.englishmaster_be.entity.UserEntity;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IFileStorageService;
import com.example.englishmaster_be.Service.IPartService;
import com.example.englishmaster_be.Service.IUserService;
import com.google.cloud.storage.Blob;
import lombok.AccessLevel;
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
    public PartEntity savePart(PartRequest partRequest) {

        UserEntity user = userService.currentUser();

        PartEntity partEntity;

        String messageBadRequestException = "Create PartEntity fail: The PartEntity name is already exist";

        if(partRequest.getPartId() != null){

            partEntity = getPartToId(partRequest.getPartId());

            if(isExistedPartNameWithDiff(partEntity, partRequest.getPartName()))
                throw new BadRequestException(messageBadRequestException);
        }
        else{

            if(isExistedPartName(partRequest.getPartName()))
                throw new BadRequestException(messageBadRequestException);

            partEntity = PartEntity.builder()
                    .createAt(LocalDateTime.now())
                    .userCreate(user)
                    .build();
        }

        if(partRequest.getFile() != null && !partRequest.getFile().isEmpty()){

            Blob blobResponse = fileStorageService.save(partRequest.getFile());

            String fileName = blobResponse.getName();
            String contentType = blobResponse.getContentType();

            partEntity.setContentData(fileName);
            partEntity.setContentType(contentType);
        }

        PartMapper.INSTANCE.flowToPartEntity(partRequest, partEntity);

        partEntity.setUpdateAt(LocalDateTime.now());
        partEntity.setUserUpdate(user);

        return partRepository.save(partEntity);
    }


    @Override
    public List<PartEntity> getListPart() {

        return partRepository.findAll();
    }

    @Override
    public PartEntity getPartToId(UUID partId) {
        return partRepository.findByPartId(partId)
                .orElseThrow(
                        () -> new CustomException(ErrorEnum.PART_NOT_FOUND)
                );
    }

    @Override
    public PartEntity getPartToName(String partName) {

        List<PartEntity> listPart = partRepository.findAll();

        return listPart.stream().filter(
                part -> part.getPartName().substring(0, 6).equalsIgnoreCase(partName)
        ).findFirst().orElseThrow(
                () -> new CustomException(ErrorEnum.PART_NOT_FOUND)
        );
    }

    @Override
    public boolean isExistedPartNameWithDiff(PartEntity part, String partName) {

        return partRepository.isExistedPartNameWithDiff(part, partName);
    }

    @Override
    public boolean isExistedPartName(String partName) {

        return partRepository.findByPartName(partName).isPresent();
    }

    @Override
    public void deletePart(UUID partId) {

        PartEntity partEntity = getPartToId(partId);

        partRepository.delete(partEntity);
    }


    @Transactional
    @Override
    public PartEntity uploadFilePart(UUID partId, UploadMultiFileRequest uploadMultiFileRequest) {

        UserEntity user = userService.currentUser();

        PartEntity partEntity = getPartToId(partId);

        if(
                uploadMultiFileRequest == null
                        || uploadMultiFileRequest.getContentData() == null
                        || uploadMultiFileRequest.getContentData().isEmpty()
        ) throw new CustomException(ErrorEnum.NULL_OR_EMPTY_FILE);


        for(MultipartFile file : uploadMultiFileRequest.getContentData()){

            if(file == null || file.isEmpty()) continue;

            if (partEntity.getContentType() != null && !partEntity.getContentType().isEmpty())
                fileStorageService.delete(partEntity.getContentData());

            Blob blobResponse = fileStorageService.save(file);

            partEntity.setContentType(GetExtension.typeFile(blobResponse.getName()));
            partEntity.setContentData(blobResponse.getName());
            partEntity.setUserUpdate(user);
            partEntity.setUpdateAt(LocalDateTime.now());
        }

        return partRepository.save(partEntity);
    }


    @Transactional
    @Override
    public PartEntity uploadTextPart(UUID partId, UploadTextRequest uploadTextDTO) {

        UserEntity user = userService.currentUser();

        PartEntity partEntity = getPartToId(partId);

        partEntity.setContentData(uploadTextDTO.getContentData());
        partEntity.setContentType(uploadTextDTO.getContentType());
        partEntity.setUserUpdate(user);
        partEntity.setUpdateAt(LocalDateTime.now());

        return partRepository.save(partEntity);
    }
}

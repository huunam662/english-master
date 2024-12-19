package com.example.englishmaster_be.domain.part.service;

import com.example.englishmaster_be.common.constant.error.ErrorEnum;
import com.example.englishmaster_be.domain.cloudinary.dto.response.CloudiaryUploadFileResponse;
import com.example.englishmaster_be.domain.cloudinary.service.ICloudinaryService;
import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import com.example.englishmaster_be.util.GetExtensionUtil;
import com.example.englishmaster_be.domain.part.dto.request.PartRequest;
import com.example.englishmaster_be.model.part.PartRepository;
import com.example.englishmaster_be.shared.upload_file.dto.request.UploadMultipleFileRequest;
import com.example.englishmaster_be.domain.part.dto.request.PartSaveContentRequest;
import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.mapper.PartMapper;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.domain.file_storage.service.IFileStorageService;
import com.example.englishmaster_be.domain.user.service.IUserService;
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
public class PartService implements IPartService {

    PartRepository partRepository;

    IUserService userService;

    IFileStorageService fileStorageService;


    @Transactional
    @Override
    public PartEntity savePart(PartRequest partRequest) {

        UserEntity user = userService.currentUser();

        PartEntity partEntity;

        String messageBadRequestException = "Create part fail: The part name is already exist";

        if(partRequest.getPartId() != null){

            partEntity = getPartToId(partRequest.getPartId());

            if(isExistedPartNameWithDiff(partEntity, partRequest.getPartName()))
                throw new BadRequestException(messageBadRequestException);
        }
        else{

            if(isExistedPartName(partRequest.getPartName()))
                throw new BadRequestException(messageBadRequestException);

            partEntity = PartEntity.builder()
                    .userCreate(user)
                    .build();
        }

        if(partRequest.getFile() != null && !partRequest.getFile().isEmpty()){

            FileResponse fileResponse = fileStorageService.save(partRequest.getFile());

            partEntity.setContentData(fileResponse.getFileName());
            partEntity.setContentType(fileResponse.getContentType());
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
    public PartEntity uploadFilePart(UUID partId, MultipartFile contentData) {

        if(
                contentData == null
                        ||
                        contentData.isEmpty()
        ) throw new CustomException(ErrorEnum.NULL_OR_EMPTY_FILE);

        UserEntity user = userService.currentUser();

        PartEntity partEntity = getPartToId(partId);

        FileResponse fileResponse = fileStorageService.save(contentData);

        partEntity.setContentType(fileResponse.getContentType());
        partEntity.setContentData(fileResponse.getFileName());
        partEntity.setUserUpdate(user);


        return partRepository.save(partEntity);
    }


    @Transactional
    @Override
    public PartEntity uploadTextPart(UUID partId, PartSaveContentRequest uploadTextDTO) {

        UserEntity user = userService.currentUser();

        PartEntity partEntity = getPartToId(partId);

        partEntity.setContentData(uploadTextDTO.getContentData());
        partEntity.setContentType(uploadTextDTO.getContentType());
        partEntity.setUserUpdate(user);
        partEntity.setUpdateAt(LocalDateTime.now());

        return partRepository.save(partEntity);
    }
}

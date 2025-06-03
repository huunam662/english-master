package com.example.englishmaster_be.domain.part.service;

import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestPartRequest;
import com.example.englishmaster_be.domain.part.dto.request.CreatePartQuestionsAnswersRequest;
import com.example.englishmaster_be.domain.part.dto.request.EditPartQuestionsAnswersRequest;
import com.example.englishmaster_be.domain.part.dto.response.PartKeyResponse;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
import com.example.englishmaster_be.domain.part.dto.request.PartRequest;
import com.example.englishmaster_be.model.part.PartRepository;
import com.example.englishmaster_be.domain.part.dto.request.PartSaveContentRequest;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.mapper.PartMapper;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.part.QPartEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.helper.FileHelper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartService implements IPartService {

    FileHelper fileUtil;

    JPAQueryFactory jpaQueryFactory;

    PartRepository partRepository;

    IUserService userService;

    IUploadService uploadService;

    IQuestionService questionService;



    @Override
    public PartEntity getPartByPartNameTopicId(String partName, UUID topicId) {

        Assert.notNull(partName, "Part name is required.");
        Assert.notNull(topicId, "Topic id is required.");

        return partRepository.findPartByPartNameTopicId(partName, topicId)
                .orElseThrow(() -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Part " +partName+" not found."));
    }

    @Transactional
    @Override
    @SneakyThrows
    public PartEntity savePart(PartRequest partRequest) {

        UserEntity user = userService.currentUser();

        PartEntity partEntity;

        String messageBadRequestException = "Create part fail: The part name is already exist";

        if(partRequest.getPartId() != null){

            partEntity = getPartToId(partRequest.getPartId());

            if(isExistedPartNameWithDiff(partEntity, partRequest.getPartName()))
                throw new ErrorHolder(Error.CONFLICT, messageBadRequestException);
        }
        else{

            if(isExistedPartName(partRequest.getPartName()))
                throw new ErrorHolder(Error.CONFLICT, messageBadRequestException);

            partEntity = PartEntity.builder()
                    .userCreate(user)
                    .build();
        }

        if(partRequest.getFile() != null && !partRequest.getFile().isEmpty()){

            if(partEntity.getContentData() != null && !partEntity.getContentData().isEmpty())
                uploadService.delete(
                        FileDeleteRequest.builder()
                                .filepath(partEntity.getContentData())
                                .build()
                );

            partEntity.setContentData(partRequest.getFile());
            partEntity.setContentType(fileUtil.mimeTypeFile(partRequest.getFile()));
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
                        () -> new ErrorHolder(Error.PART_NOT_FOUND)
                );
    }

    @Override
    public PartEntity getPartToName(String partName, String partType, TopicEntity topicEntity){

        PartEntity partEntity = jpaQueryFactory.selectFrom(QPartEntity.partEntity)
                .where(
                        QPartEntity.partEntity.partName.equalsIgnoreCase(partName)
                                .and(QPartEntity.partEntity.partType.equalsIgnoreCase(partType))
                                .and(QPartEntity.partEntity.topics.contains(topicEntity))
                ).fetchOne();

        return Optional.ofNullable(partEntity).orElseThrow(
                () -> new ErrorHolder(Error.PART_NOT_FOUND)
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

    @Override
    public List<PartEntity> getPartsFromMockTestPartRequestList(List<MockTestPartRequest> mockTestPartRequestList) {

        if(mockTestPartRequestList == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "parts of mock test is null");

        return mockTestPartRequestList.stream().map(
                partMockTest -> getPartToId(partMockTest.getPartId())
        ).toList();
    }

    @Transactional
    @Override
    public PartEntity uploadFilePart(UUID partId, MultipartFile contentData) {

        if(
                contentData == null
                        ||
                        contentData.isEmpty()
        ) throw new ErrorHolder(Error.NULL_OR_EMPTY_FILE);

        UserEntity user = userService.currentUser();

        PartEntity partEntity = getPartToId(partId);

        FileResponse fileResponse = uploadService.upload(contentData);

        partEntity.setContentType(fileResponse.getUrl());
        partEntity.setContentData(fileResponse.getType());
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

    @Transactional
    @Override
    public PartKeyResponse createPartAndQuestionsAnswers(CreatePartQuestionsAnswersRequest request) {

        UserEntity userPost = userService.currentUser();

        Assert.notNull(request, "Body object request must not be null.");

        PartEntity part = partRepository.saveAndFlush(
                PartEntity.builder()
                        .partName(request.getPartName())
                        .partType(request.getPartType())
                        .partDescription(String.format("%s: %s", request.getPartName(), request.getPartType()))
                        .userCreate(userPost)
                        .userUpdate(userPost)
                        .build()
        );

        questionService.createListQuestionsParentOfPart(part, request.getQuestionParents());

        return PartKeyResponse.builder()
                .partId(part.getPartId())
                .build();
    }

    @Transactional
    @Override
    public PartKeyResponse editPartAndQuestionsAnswers(EditPartQuestionsAnswersRequest request) {

        UserEntity userPut = userService.currentUser();

        Assert.notNull(request, "Body object request must not be null.");

        PartEntity part = getPartToId(request.getPartId());
        part.setPartName(request.getPartName());
        part.setPartType(request.getPartType());
        part.setPartDescription(String.format("%s: %s", request.getPartName(), request.getPartType()));
        part.setUserUpdate(userPut);
        part = partRepository.saveAndFlush(part);

        questionService.editListQuestionsParentOfPart(part, request.getQuestionParents());

        return PartKeyResponse.builder()
                .partId(part.getPartId())
                .build();
    }

    @Override
    public List<PartEntity> getPartsFinishExam(UUID topicId, List<UUID> answerIds) {

        return partRepository.findPartJoinQuestionsAndAnswers(topicId, answerIds);
    }
}

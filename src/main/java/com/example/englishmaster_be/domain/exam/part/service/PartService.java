package com.example.englishmaster_be.domain.exam.part.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.domain.exam.part.repository.PartRepository;
import com.example.englishmaster_be.domain.exam.part.dto.req.CreatePartQuestionsAnswersReq;
import com.example.englishmaster_be.domain.exam.part.dto.req.EditPartQuestionsAnswersReq;
import com.example.englishmaster_be.domain.exam.part.dto.res.PartKeyRes;
import com.example.englishmaster_be.domain.exam.question.service.IQuestionService;
import com.example.englishmaster_be.domain.upload.meu.dto.req.FileDeleteReq;
import com.example.englishmaster_be.domain.upload.meu.service.IUploadService;
import com.example.englishmaster_be.domain.exam.part.dto.req.PartReq;
import com.example.englishmaster_be.domain.exam.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import com.example.englishmaster_be.domain.upload.util.FileUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class PartService implements IPartService {

    private final JPAQueryFactory jpaQueryFactory;
    private final PartRepository partRepository;
    private final IUserService userService;
    private final IUploadService uploadService;
    private final IQuestionService questionService;

    @Lazy
    public PartService(JPAQueryFactory jpaQueryFactory, PartRepository partRepository, IUserService userService, IUploadService uploadService, IQuestionService questionService) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.partRepository = partRepository;
        this.userService = userService;
        this.uploadService = uploadService;
        this.questionService = questionService;
    }

    @Transactional
    @Override
    @SneakyThrows
    public PartEntity savePart(PartReq partRequest) {

        UserEntity user = userService.currentUser();

        PartEntity partEntity;

        String messageBadRequestException = "Create part fail: The part name is already exist";

        if(partRequest.getPartId() != null){

            partEntity = getPartToId(partRequest.getPartId());

            if(isExistedPartNameWithDiff(partEntity, partRequest.getPartName()))
                throw new ApplicationException(HttpStatus.CONFLICT, messageBadRequestException);
        }
        else{

            if(isExistedPartName(partRequest.getPartName()))
                throw new ApplicationException(HttpStatus.CONFLICT, messageBadRequestException);

            partEntity = new PartEntity();
        }

        if(partRequest.getFile() != null && !partRequest.getFile().isEmpty()){

            if(partEntity.getContentData() != null && !partEntity.getContentData().isEmpty()) {
                FileDeleteReq deleteReq = new FileDeleteReq(partEntity.getContentData());
                CompletableFuture.runAsync(() -> {
                    try {
                        uploadService.delete(deleteReq);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            partEntity.setContentData(partRequest.getFile());
            partEntity.setContentType(FileUtil.mimeTypeFile(partRequest.getFile()));
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
                        () -> new ApplicationException(HttpStatus.NOT_FOUND, "Part not found.")
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
    public PartKeyRes createPartAndQuestionsAnswers(CreatePartQuestionsAnswersReq request) {

        PartEntity part = new PartEntity();
        part.setPartName(request.getPartName());
        part.setPartType(request.getPartType());
        part.setPartDescription(String.format("%s: %s", request.getPartName(), request.getPartType()));
        part = partRepository.saveAndFlush(part);

        questionService.createListQuestionsParentOfPart(part, request.getQuestionParents());

        return new PartKeyRes(part.getPartId());
    }

    @Transactional
    @Override
    public PartKeyRes editPartAndQuestionsAnswers(EditPartQuestionsAnswersReq request) {

        UserEntity userPut = userService.currentUser();

        PartEntity part = getPartToId(request.getPartId());
        part.setPartName(request.getPartName());
        part.setPartType(request.getPartType());
        part.setPartDescription(String.format("%s: %s", request.getPartName(), request.getPartType()));
        part.setUserUpdate(userPut);
        part = partRepository.saveAndFlush(part);

        questionService.editListQuestionsParentOfPart(part, request.getQuestionParents());

        return new PartKeyRes(part.getPartId());
    }

    @Override
    public PartEntity getPartQuestionsAnswers(UUID partId) {

        return partRepository.findPartJoinQuestionAnswer(partId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Part not found."));
    }

    @Override
    public List<PartEntity> getPartsInTopicIds(List<UUID> topicIds) {
        return partRepository.findPartsByTopicIds(topicIds);
    }
}

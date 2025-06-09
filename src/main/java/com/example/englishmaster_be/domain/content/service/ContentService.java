package com.example.englishmaster_be.domain.content.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.content.dto.request.ContentRequest;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
import com.example.englishmaster_be.domain.content.mapper.ContentMapper;
import com.example.englishmaster_be.domain.content.model.ContentEntity;
import com.example.englishmaster_be.domain.content.repository.jpa.ContentRepository;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.shared.helper.FileHelper;
import com.example.englishmaster_be.shared.util.FileUtil;
import com.example.englishmaster_be.value.LinkValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentService implements IContentService {

    LinkValue linkValue;

    ContentRepository contentRepository;

    IQuestionService questionService;

    IUserService userService;

    IUploadService uploadService;


    @Transactional
    @Override
    public void deleteContent(UUID contentId) {

        ContentEntity content = getContentByContentId(contentId);

        contentRepository.delete(content);
    }

    @Override
    public ContentEntity getContentByContentId(UUID contentId) {
        return contentRepository.findByContentId(contentId)
                .orElseThrow(
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "ContentEntity not found with ID: " + contentId, false)
                );
    }

    @Override
    public ContentEntity getContentByContentData(String contentData) {

        return contentRepository.findByContentData(contentData).orElseThrow(
                () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Get ContentEntity failed, content not found", false)
        );
    }

    @Override
    public ContentEntity getContentByTopicIdAndCode(UUID topicId, String code) {

        return contentRepository.findContentByTopicIdAndCode(topicId, code).orElseThrow(
                () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Get ContentEntity failed, content not found", false)
        );
    }


    @Transactional
    @Override
    @SneakyThrows
    public ContentEntity saveContent(ContentRequest contentRequest) {

        UserEntity user = userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(contentRequest.getQuestionId());

        ContentEntity content;

        if(contentRequest.getContentId() != null)
            content = getContentByContentId(contentRequest.getContentId());

        else content = ContentEntity.builder()
                .userCreate(user)
                .build();

        ContentMapper.INSTANCE.flowToContentEntity(contentRequest, content);

        if(contentRequest.getImage() != null){

            if(content.getContentData() != null && !content.getContentData().isEmpty())
                uploadService.delete(
                        FileDeleteRequest.builder()
                                .filepath(content.getContentData())
                                .build()
                );

            content.setContentData(contentRequest.getImage());
            content.setContentType(FileUtil.mimeTypeFile(contentRequest.getImage()));
        }

        if(!content.getQuestions().contains(question))
            content.getQuestions().add(question);

        content.setUserUpdate(user);

        return contentRepository.save(content);
    }

    @Override
    public List<String> getImageCdnLink() {

        List<String> listLinkCdn = contentRepository.findAllContentData();

        return listLinkCdn.stream()
                .filter(linkCdn -> linkCdn != null && !linkCdn.isEmpty())
                .map(linkCdn -> linkValue.getLinkFileShowImageBE() + linkCdn)
                .toList();
    }



}

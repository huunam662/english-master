package com.example.englishmaster_be.domain.content.service;

import com.example.englishmaster_be.common.thread.MessageResponseHolder;
import com.example.englishmaster_be.domain.content.dto.request.ContentRequest;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
import com.example.englishmaster_be.advice.exception.template.BadRequestException;
import com.example.englishmaster_be.converter.ContentConverter;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.content.ContentRepository;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.helper.FileHelper;
import com.example.englishmaster_be.value.LinkValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentService implements IContentService {

    LinkValue linkValue;

    FileHelper fileUtil;

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
                        () -> new IllegalArgumentException("ContentEntity not found with ID: " + contentId)
                );
    }

    @Override
    public ContentEntity getContentByContentData(String contentData) {

        return contentRepository.findByContentData(contentData).orElseThrow(
                () -> new BadRequestException("Get ContentEntity failed, content not found")
        );
    }

    @Override
    public ContentEntity getContentByTopicIdAndCode(UUID topicId, String code) {

        return contentRepository.findContentByTopicIdAndCode(topicId, code).orElseThrow(
                () -> new BadRequestException("Get ContentEntity failed, content not found")
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

        ContentConverter.INSTANCE.flowToContentEntity(contentRequest, content);

        if(contentRequest.getImage() != null){

            if(content.getContentData() != null && !content.getContentData().isEmpty())
                uploadService.delete(
                        FileDeleteRequest.builder()
                                .filepath(content.getContentData())
                                .build()
                );

            content.setContentData(contentRequest.getImage());
            content.setContentType(fileUtil.mimeTypeFile(contentRequest.getImage()));
        }

        if(!content.getQuestions().contains(question))
            content.getQuestions().add(question);

        content.setUserUpdate(user);

        return contentRepository.save(content);
    }

    @Override
    public List<String> getImageCdnLink() {

        List<String> listLinkCdn = contentRepository.findAllContentData();

        MessageResponseHolder.setMessage("Found " + listLinkCdn.size() + " links");

        return listLinkCdn.stream()
                .filter(linkCdn -> linkCdn != null && !linkCdn.isEmpty())
                .map(linkCdn -> linkValue.getLinkFileShowImageBE() + linkCdn)
                .toList();
    }



}

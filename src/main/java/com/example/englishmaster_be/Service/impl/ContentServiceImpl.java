package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.Model.Request.Content.ContentRequest;
import com.example.englishmaster_be.Exception.template.BadRequestException;
import com.example.englishmaster_be.Mapper.ContentMapper;
import com.example.englishmaster_be.entity.ContentEntity;
import com.example.englishmaster_be.entity.QuestionEntity;
import com.example.englishmaster_be.Model.Response.CloudiaryUploadFileResponse;
import com.example.englishmaster_be.entity.UserEntity;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.ICloudinaryService;
import com.example.englishmaster_be.Service.IContentService;
import com.example.englishmaster_be.Service.IQuestionService;
import com.example.englishmaster_be.Service.IUserService;
import com.example.englishmaster_be.Util.LinkUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentServiceImpl implements IContentService {

    ContentRepository contentRepository;

    ICloudinaryService cloudinaryService;

    IQuestionService questionService;

    IUserService userService;


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
    public ContentEntity saveContent(ContentRequest contentRequest) {

        UserEntity user = userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(contentRequest.getQuestionId());

        ContentEntity content;

        if(contentRequest.getContentId() != null)
            content = getContentByContentId(contentRequest.getContentId());

        else content = ContentEntity.builder()
                .createAt(LocalDateTime.now())
                .userCreate(user)
                .build();

        ContentMapper.INSTANCE.flowToContentEntity(contentRequest, content);

        content.setQuestion(question);
        content.setUpdateAt(LocalDateTime.now());
        content.setUserUpdate(user);

        if(contentRequest.getFile() != null){

            CloudiaryUploadFileResponse cloudiaryUploadFileResponse = cloudinaryService.uploadFile(contentRequest.getFile());

            content.setContentType(cloudiaryUploadFileResponse.getType());
            content.setContentData(cloudiaryUploadFileResponse.getUrl());

        }

        return contentRepository.save(content);
    }

    @Override
    public List<String> getImageCdnLink() {

        List<String> listLinkCdn = contentRepository.findAllContentData();

        MessageResponseHolder.setMessage("Found " + listLinkCdn.size() + " links");

        return listLinkCdn.stream()
                .filter(linkCdn -> linkCdn != null && !linkCdn.isEmpty())
                .map(linkCdn -> LinkUtil.linkFileShowImageBE + linkCdn)
                .toList();
    }


}

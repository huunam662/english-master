package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.DTO.Content.SaveContentDTO;
import com.example.englishmaster_be.DTO.Content.UpdateContentDTO;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Model.Content;
import com.example.englishmaster_be.Model.Response.CloudiaryUploadFileResponse;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.ICloudinaryService;
import com.example.englishmaster_be.Service.IContentService;
import com.example.englishmaster_be.Service.IQuestionService;
import com.example.englishmaster_be.Util.LinkUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
public class ContentServiceImpl implements IContentService {

    ContentRepository contentRepository;

    ICloudinaryService cloudinaryService;

    IQuestionService questionService;


    @Transactional
    @Override
    public void delete(UUID contentId) {

        Content content = getContentToContentId(contentId);

        contentRepository.delete(content);
    }

    @Override
    public Content getContentToContentId(UUID contentId) {
        return contentRepository.findByContentId(contentId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Content not found with ID: " + contentId)
                );
    }

    @Override
    public Content getContentByContentData(String contentData) {

        return contentRepository.findByContentData(contentData).orElseThrow(
                () -> new BadRequestException("Get Content failed, content not found")
        );
    }

    @Override
    public Content getContentByTopicIdAndCode(UUID topicId, String code) {

        return contentRepository.findContentByTopicIdAndCode(topicId, code).orElseThrow(
                () -> new BadRequestException("Get Content failed, content not found")
        );
    }


    @Transactional
    @Override
    public Content saveContent(SaveContentDTO createContentDTO) {

        Content content;

        if(createContentDTO instanceof UpdateContentDTO updateContentDTO) {

            content = getContentToContentId(updateContentDTO.getContentId());
            content.setContentType(updateContentDTO.getContentType());
            content.setCode(updateContentDTO.getCode());
            content.setTopicId(updateContentDTO.getTopicId());
        }
        else content = Content.builder()
                .contentType(createContentDTO.getContentType())
                .code(createContentDTO.getCode())
                .topicId(createContentDTO.getTopicId())
                .build();

        content.setQuestion(questionService.getQuestionById(createContentDTO.getQuestionId()));

        if(createContentDTO.getFile() != null){

            CloudiaryUploadFileResponse cloudiaryUploadFileResponse = cloudinaryService.uploadFile(createContentDTO.getFile());

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

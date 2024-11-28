package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Content.CreateContentDTO;
import com.example.englishmaster_be.DTO.Content.UpdateContentDTO;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Model.Content;
import com.example.englishmaster_be.Model.Response.CloudiaryUploadFileResponse;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.ICloudinaryService;
import com.example.englishmaster_be.Service.IContentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentServiceImpl implements IContentService {

    ContentRepository contentRepository;

    ICloudinaryService cloudinaryService;

    @Transactional
    @Override
    public void uploadContent(Content content) {
        contentRepository.save(content);
    }

    @Transactional
    @Override
    public void delete(Content content) {
        contentRepository.delete(content);
    }

    @Override
    public Content getContentToContentId(UUID contentId) {
        return contentRepository.findByContentId(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found with ID: " + contentId));
    }


    @Transactional
    @Override
    public Content saveContent(CreateContentDTO createContentDTO) {

        if(createContentDTO instanceof UpdateContentDTO updateContentDTO) {

            Content content = new Content(updateContentDTO);

            if(updateContentDTO.getFile() != null){

                CloudiaryUploadFileResponse cloudiaryUploadFileResponse = cloudinaryService.uploadFile(updateContentDTO.getFile());

                content.setContentData(cloudiaryUploadFileResponse.getUrl());

            }

            return contentRepository.save(content);

        }
        else return null;
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
}

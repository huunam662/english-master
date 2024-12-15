package com.example.englishmaster_be.domain.content.service;

import com.example.englishmaster_be.domain.content.dto.request.ContentRequest;
import com.example.englishmaster_be.model.content.ContentEntity;

import java.util.List;
import java.util.UUID;


public interface IContentService {

    void deleteContent(UUID contentId);

    ContentEntity getContentByTopicIdAndCode(UUID topicId, String code);

    ContentEntity getContentByContentId(UUID contentId);

    ContentEntity getContentByContentData(String contentData);

    ContentEntity saveContent(ContentRequest contentRequest);

    List<String> getImageCdnLink();

}

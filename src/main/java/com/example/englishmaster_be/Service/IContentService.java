package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Request.Content.ContentRequest;
import com.example.englishmaster_be.entity.ContentEntity;

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

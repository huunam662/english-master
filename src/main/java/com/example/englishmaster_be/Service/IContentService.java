package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Content.CreateContentDTO;
import com.example.englishmaster_be.DTO.Content.UpdateContentDTO;
import com.example.englishmaster_be.Model.Content;
import com.example.englishmaster_be.Model.Response.ContentResponse;

import java.util.List;
import java.util.UUID;


public interface IContentService {

    void uploadContent(Content content);

    void delete(Content content);

    Content getContentByTopicIdAndCode(UUID topicId, String code);

    Content getContentToContentId(UUID contentId);

    Content getContentByContentData(String contentData);

    Content saveContent(CreateContentDTO createContentDTO);

    List<String> getImageCdnLink();

}

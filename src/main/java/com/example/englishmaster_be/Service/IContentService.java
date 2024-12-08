package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Content.SaveContentDTO;
import com.example.englishmaster_be.Model.Content;

import java.util.List;
import java.util.UUID;


public interface IContentService {

    void delete(UUID contentId);

    Content getContentByTopicIdAndCode(UUID topicId, String code);

    Content getContentToContentId(UUID contentId);

    Content getContentByContentData(String contentData);

    Content saveContent(SaveContentDTO createContentDTO);

    List<String> getImageCdnLink();

}

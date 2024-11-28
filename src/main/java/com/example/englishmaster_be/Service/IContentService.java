package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Content;

import java.util.UUID;


public interface IContentService {
    void uploadContent(Content content);
    void delete(Content content);
    Content getContentToContentId(UUID contentId);

}

package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.Content;
import com.example.englishmaster_be.model.Question;

import java.util.List;
import java.util.UUID;


public interface IContentService {
    void uploadContent(Content content);
    void delete(Content content);
    Content getContentToContentId(UUID contentId);

}

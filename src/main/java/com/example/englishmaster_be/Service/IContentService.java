package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Content;
import com.example.englishmaster_be.Model.Question;

import java.util.List;
import java.util.UUID;


public interface IContentService {
    void uploadContent(Content content);
    Content getContentToContentId(UUID contentId);

}

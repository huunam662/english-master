package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.model.Content;
import com.example.englishmaster_be.repository.*;
import com.example.englishmaster_be.service.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ContentServiceImpl implements IContentService {
    @Autowired
    private ContentRepository contentRepository;

    @Transactional
    @Override
    public void uploadContent(Content content) {
        contentRepository.save(content);
    }

    @Override
    public void delete(Content content) {
        contentRepository.delete(content);
    }

    @Override
    public Content getContentToContentId(UUID contentId) {
        return contentRepository.findByContentId(contentId)
                .orElseThrow(() -> new IllegalArgumentException("content not found with ID: " + contentId));
    }
}

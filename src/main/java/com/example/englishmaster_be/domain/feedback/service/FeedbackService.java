package com.example.englishmaster_be.domain.feedback.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.feedback.dto.view.IFeedbackPageView;
import com.example.englishmaster_be.domain.feedback.dto.req.FeedbackReq;
import com.example.englishmaster_be.domain.feedback.repository.FeedbackDslRepository;
import com.example.englishmaster_be.domain.upload.meu.dto.req.FileDeleteReq;
import com.example.englishmaster_be.domain.upload.meu.service.IUploadService;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import com.example.englishmaster_be.domain.feedback.mapper.FeedbackMapper;
import com.example.englishmaster_be.domain.feedback.repository.FeedbackRepository;
import com.example.englishmaster_be.domain.feedback.model.FeedbackEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class FeedbackService implements IFeedbackService {

    private final FeedbackDslRepository feedbackDslRepository;
    private final FeedbackRepository feedbackRepository;
    private final IUserService userService;
    private final IUploadService uploadService;

    @Lazy
    public FeedbackService(FeedbackDslRepository feedbackDslRepository, FeedbackRepository feedbackRepository, IUserService userService, IUploadService uploadService) {
        this.feedbackDslRepository = feedbackDslRepository;
        this.feedbackRepository = feedbackRepository;
        this.userService = userService;
        this.uploadService = uploadService;
    }

    @Override
    public FeedbackEntity getFeedbackById(UUID feedbackId) {
        return feedbackRepository.findByFeedbackId(feedbackId)
                .orElseThrow(
                        () -> new ApplicationException(HttpStatus.NOT_FOUND, "Feedback not found")
                );
    }

    @Override
    public Page<IFeedbackPageView> getPageFeedback(PageOptionsReq optionsReq) {
        return feedbackDslRepository.findPageFeedback(optionsReq);
    }

    @Transactional
    @Override
    public FeedbackEntity saveFeedback(FeedbackReq feedbackRequest) {

        UserEntity user = userService.currentUser();

        FeedbackEntity feedback;

        if(feedbackRequest.getFeedbackId() != null)
            feedback = getFeedbackById(feedbackRequest.getFeedbackId());

        else feedback = new FeedbackEntity();

        FeedbackMapper.INSTANCE.flowToFeedbackEntity(feedbackRequest, feedback);
        feedback.setAvatar(user.getAvatar());
        feedback.setEnable(Boolean.TRUE);

        return feedbackRepository.save(feedback);
    }

    @Transactional
    @Override
    public void enableFeedback(UUID feedbackId, Boolean enable) {
        
        FeedbackEntity feedback = getFeedbackById(feedbackId);

        feedback.setEnable(enable);

        feedbackRepository.save(feedback);
    }

    @Transactional
    @Override
    @SneakyThrows
    public void deleteFeedback(UUID feedbackId) {

        FeedbackEntity feedback = getFeedbackById(feedbackId);

        if(feedback.getAvatar() != null)
            CompletableFuture.runAsync(() -> {
                try {
                    uploadService.delete(new FileDeleteReq(feedback.getAvatar()));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });

        feedbackRepository.delete(feedback);
    }
}

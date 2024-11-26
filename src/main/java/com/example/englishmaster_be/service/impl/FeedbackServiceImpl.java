package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.model.Feedback;
import com.example.englishmaster_be.repository.*;
import com.example.englishmaster_be.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FeedbackServiceImpl implements IFeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Override
    public void save(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

    @Override
    public void delete(Feedback feedback) {
        feedbackRepository.delete(feedback);
    }

    @Override
    public Feedback findFeedbackById(UUID feedbackId) {
        return feedbackRepository.findByFeedbackId(feedbackId)
                .orElseThrow(() -> new IllegalArgumentException("feedback not found with ID: " + feedbackId));
    }
}

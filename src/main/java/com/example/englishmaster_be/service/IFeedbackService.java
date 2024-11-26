package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.*;

import java.util.UUID;

public interface IFeedbackService {
    void save(Feedback feedback);

    void delete(Feedback feedback);

    Feedback findFeedbackById(UUID feedbackId);
}

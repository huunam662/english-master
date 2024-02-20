package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.*;

import java.util.UUID;

public interface IFeedbackService {
    void save(Feedback feedback);

    void delete(Feedback feedback);

    Feedback findFeedbackById(UUID feedbackId);
}

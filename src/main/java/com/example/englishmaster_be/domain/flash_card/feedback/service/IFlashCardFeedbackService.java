package com.example.englishmaster_be.domain.flash_card.feedback.service;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.req.FlashCardFeedbackReq;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.view.IFlashCardFeedbackPageView;
import com.example.englishmaster_be.domain.flash_card.feedback.model.FlashCardFeedbackEntity;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface IFlashCardFeedbackService {

    FlashCardFeedbackEntity getSingleFlashCardFeedback(UUID id);

    List<FlashCardFeedbackEntity> getListFlashCardFeedback();

    FlashCardFeedbackEntity createFlashCardFeedback(UUID flashCardId, FlashCardFeedbackReq body);

    FlashCardFeedbackEntity editFlashCardFeedbackContent(UUID id, FlashCardFeedbackReq body);

    void deleteSingleFlashCardFeedback(UUID id);

    Page<IFlashCardFeedbackPageView> getPageFlashCardFeedback(PageOptionsReq optionsReq);

    Page<IFlashCardFeedbackPageView> getPageFlashCardFeedbackToFlashCardId(UUID flashCardId, PageOptionsReq optionsReq);

}

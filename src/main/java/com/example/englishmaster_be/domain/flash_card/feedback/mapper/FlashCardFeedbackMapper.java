package com.example.englishmaster_be.domain.flash_card.feedback.mapper;

import com.example.englishmaster_be.domain.flash_card.feedback.dto.res.FlashCardFeedbackFbRes;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.res.FlashCardFeedbackRes;
import com.example.englishmaster_be.domain.flash_card.feedback.model.FlashCardFeedbackEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FlashCardFeedbackMapper {

    FlashCardFeedbackMapper INSTANCE = Mappers.getMapper(FlashCardFeedbackMapper.class);

    FlashCardFeedbackRes toFlashCardFeedbackRes(FlashCardFeedbackEntity flashCardFeedback);

    List<FlashCardFeedbackRes> toFlashCardFeedbackResList(List<FlashCardFeedbackEntity> flashCardFeedbacks);

    FlashCardFeedbackFbRes toFlashCardFeedbackFbRes(FlashCardFeedbackEntity flashCardFeedback);

    List<FlashCardFeedbackFbRes> toFlashCardFeedbackFbResList(List<FlashCardFeedbackEntity> flashCardFeedbacks);

}

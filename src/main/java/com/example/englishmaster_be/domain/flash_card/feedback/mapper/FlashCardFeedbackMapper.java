package com.example.englishmaster_be.domain.flash_card.feedback.mapper;

import com.example.englishmaster_be.domain.flash_card.feedback.dto.res.FlashCardFeedbackFullRes;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.res.FlashCardFeedbackPageRes;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.view.IFlashCardFeedbackPageView;
import com.example.englishmaster_be.domain.flash_card.feedback.model.FlashCardFeedbackEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper
public interface FlashCardFeedbackMapper {

    FlashCardFeedbackMapper INSTANCE = Mappers.getMapper(FlashCardFeedbackMapper.class);

    FlashCardFeedbackFullRes toFlashCardFeedbackFullRes(FlashCardFeedbackEntity flashCardFeedback);

    List<FlashCardFeedbackFullRes> toFlashCardFeedbackFullResList(Collection<FlashCardFeedbackEntity> flashCardFeedbacks);

    FlashCardFeedbackPageRes toFlashCardFeedbackPageRes(IFlashCardFeedbackPageView flashCardFeedbackPageView);

    List<FlashCardFeedbackPageRes> toFlashCardFeedbackPageResList(Collection<IFlashCardFeedbackPageView> flashCardFeedbackPageViews);

}

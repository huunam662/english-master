package com.example.englishmaster_be.domain.flash_card.flash_card.mapper;

import com.example.englishmaster_be.domain.flash_card.flash_card.dto.res.FlashCardFullRes;
import com.example.englishmaster_be.domain.flash_card.flash_card.dto.res.FlashCardPageRes;
import com.example.englishmaster_be.domain.flash_card.flash_card.dto.view.IFlashCardPageView;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper
public interface FlashCardMapper {

    FlashCardMapper INSTANCE = Mappers.getMapper(FlashCardMapper.class);

    FlashCardFullRes toFlashCardRes(FlashCardEntity flashCard);

    List<FlashCardFullRes> toFlashCardResList(Collection<FlashCardEntity> flashCards);

    FlashCardPageRes toFlashCardPageRes(IFlashCardPageView flashCardPageView);

    List<FlashCardPageRes> toFlashCardPageResList(Collection<IFlashCardPageView> flashCardPageViews);

}

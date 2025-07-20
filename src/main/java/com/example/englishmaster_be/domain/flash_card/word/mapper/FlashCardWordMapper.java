package com.example.englishmaster_be.domain.flash_card.word.mapper;

import com.example.englishmaster_be.domain.flash_card.word.dto.res.FlashCardWordPageRes;
import com.example.englishmaster_be.domain.flash_card.word.dto.res.FlashCardWordRes;
import com.example.englishmaster_be.domain.flash_card.word.dto.view.IFlashCardWordPageView;
import com.example.englishmaster_be.domain.flash_card.word.model.FlashCardWordEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper
public interface FlashCardWordMapper {

    FlashCardWordMapper INSTANCE = Mappers.getMapper(FlashCardWordMapper.class);

    FlashCardWordRes toFlashCardWordRes(FlashCardWordEntity flashCardWord);

    List<FlashCardWordRes> toFlashCardWordResList(Collection<FlashCardWordEntity> flashCardWords);

    FlashCardWordPageRes toFlashCardWordPageRes(IFlashCardWordPageView flashCardWordPageView);

    List<FlashCardWordPageRes> toFlashCardWordPageResList(Collection<IFlashCardWordPageView> flashCardWordPageViews);

}

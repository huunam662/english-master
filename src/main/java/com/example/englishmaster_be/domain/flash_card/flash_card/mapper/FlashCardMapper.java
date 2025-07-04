package com.example.englishmaster_be.domain.flash_card.flash_card.mapper;

import com.example.englishmaster_be.domain.flash_card.flash_card.dto.response.FlashCardRes;
import com.example.englishmaster_be.domain.flash_card.flash_card.dto.response.FlashCardUserRes;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FlashCardMapper {

    FlashCardMapper INSTANCE = Mappers.getMapper(FlashCardMapper.class);

    FlashCardRes toFlashCardRes(FlashCardEntity flashCard);

    List<FlashCardRes> toFlashCardResList(List<FlashCardEntity> flashCards);

    FlashCardUserRes toFlashCardUserRes(FlashCardEntity flashCard);

    List<FlashCardUserRes> toFlashCardUserResList(List<FlashCardEntity> flashCards);
}

package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.Model.Request.FlashCard.FlashCardRequest;
import com.example.englishmaster_be.entity.FlashCardEntity;
import com.example.englishmaster_be.Model.Response.FlashCardListWordResponse;
import com.example.englishmaster_be.Model.Response.FlashCardResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FlashCardMapper {

    FlashCardMapper INSTANCE = Mappers.getMapper(FlashCardMapper.class);

    FlashCardResponse toFlashCardResponse(FlashCardEntity flashCard);

    List<FlashCardResponse> toFlashCardResponseList(List<FlashCardEntity> flashCardList);

    @Mapping(target = "flashCardWords", expression = "java(FlashCardWordMapper.INSTANCE.toFlashCardWordResponseList(flashCard.getFlashCardWords()))")
    FlashCardListWordResponse toFlashCardListWordResponse(FlashCardEntity flashCard);

    @Mapping(target = "flashCardImage", ignore = true)
    @Mapping(target = "flashCardId", ignore = true)
    void flowToFlashCardEntity(FlashCardRequest flashCardRequest, @MappingTarget FlashCardEntity flashCard);

}

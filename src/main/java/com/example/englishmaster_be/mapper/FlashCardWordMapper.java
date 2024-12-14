package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.model.request.FlashCard.FlashCardWordRequest;
import com.example.englishmaster_be.entity.FlashCardWordEntity;
import com.example.englishmaster_be.model.response.FlashCardWordResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FlashCardWordMapper {

    FlashCardWordMapper INSTANCE = Mappers.getMapper(FlashCardWordMapper.class);

    @Mapping(target = "flashCardId", source = "flashCard.flashCardId")
    FlashCardWordResponse toFlashCardWordResponse(FlashCardWordEntity flashCardWord);

    @Mapping(target = "image", ignore = true)
    FlashCardWordEntity toFlashCardWord(FlashCardWordRequest flashCardWordRequest);

    List<FlashCardWordResponse> toFlashCardWordResponseList(List<FlashCardWordEntity> flashCardWords);

    @Mapping(target = "image", ignore = true)
    void flowToFlashCardWordEntity(FlashCardWordRequest flashCardWordRequest, @MappingTarget FlashCardWordEntity flashCardWord);
}

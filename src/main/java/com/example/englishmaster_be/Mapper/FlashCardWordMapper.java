package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.Model.Request.FlashCard.FlashCardWordRequest;
import com.example.englishmaster_be.entity.FlashCardWordEntity;
import com.example.englishmaster_be.Model.Response.FlashCardWordResponse;
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

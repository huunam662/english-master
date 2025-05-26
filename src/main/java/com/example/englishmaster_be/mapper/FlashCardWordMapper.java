package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.flash_card_word.dto.request.FlashCardWordRequest;
import com.example.englishmaster_be.model.flash_card_word.FlashCardWordEntity;
import com.example.englishmaster_be.domain.flash_card_word.dto.response.FlashCardWordResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface FlashCardWordMapper {

    FlashCardWordMapper INSTANCE = Mappers.getMapper(FlashCardWordMapper.class);

    @Mapping(target = "flashCardId", source = "flashCard.flashCardId")
    FlashCardWordResponse toFlashCardWordResponse(FlashCardWordEntity flashCardWord);

    @Mapping(target = "image", ignore = true)
    FlashCardWordEntity toFlashCardWord(FlashCardWordRequest flashCardWordRequest);

    List<FlashCardWordResponse> toFlashCardWordResponseList(Collection<FlashCardWordEntity> flashCardWords);

    @Mapping(target = "image", ignore = true)
    void flowToFlashCardWordEntity(FlashCardWordRequest flashCardWordRequest, @MappingTarget FlashCardWordEntity flashCardWord);
}

package com.example.englishmaster_be.domain.flash_card.mapper;

import com.example.englishmaster_be.domain.flash_card.dto.request.FlashCardRequest;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardUserResponse;
import com.example.englishmaster_be.domain.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.flash_card_word.dto.response.FlashCardWordListResponse;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardResponse;
import com.example.englishmaster_be.domain.flash_card_word.mapper.FlashCardWordMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(imports = {FlashCardWordMapper.class}, builder = @Builder(disableBuilder = true))
public interface FlashCardMapper {

    FlashCardMapper INSTANCE = Mappers.getMapper(FlashCardMapper.class);

    FlashCardResponse toFlashCardResponse(FlashCardEntity flashCard);

    List<FlashCardResponse> toFlashCardResponseList(Collection<FlashCardEntity> flashCardList);

    FlashCardUserResponse toFlashCardUserResponse(FlashCardEntity flashCard);

    List<FlashCardUserResponse> toFlashCardUserResponseList(Collection<FlashCardEntity> flashCardList);

    @Mapping(target = "flashCardWords", expression = "java(FlashCardWordMapper.INSTANCE.toFlashCardWordResponseList(flashCard.getFlashCardWords()))")
    FlashCardWordListResponse toFlashCardListWordResponse(FlashCardEntity flashCard);

    @Mapping(target = "flashCardId", ignore = true)
    void flowToFlashCardEntity(FlashCardRequest flashCardRequest, @MappingTarget FlashCardEntity flashCard);

    FlashCardEntity toFlashCard(FlashCardRequest request);

}

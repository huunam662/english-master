package com.example.englishmaster_be.domain.flash_card.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FlashCardKeyResponse {

    UUID flashCardId;

}

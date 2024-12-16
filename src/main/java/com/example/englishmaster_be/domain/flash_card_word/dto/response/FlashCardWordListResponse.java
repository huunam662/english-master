package com.example.englishmaster_be.domain.flash_card_word.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlashCardWordListResponse {

    String flashCardTitle;

    String flashCardDescription;

    List<FlashCardWordResponse> flashCardWords;

}

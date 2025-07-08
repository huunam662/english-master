package com.example.englishmaster_be.domain.flash_card.feedback.dto.res;

import com.example.englishmaster_be.domain.flash_card.flash_card.dto.response.FlashCardRes;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FlashCardFeedbackFbRes extends FlashCardFeedbackRes{

    FlashCardRes flashCard;

}

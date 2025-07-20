package com.example.englishmaster_be.domain.flash_card.feedback.dto.res;

import com.example.englishmaster_be.domain.flash_card.flash_card.dto.res.FlashCardRes;
import com.example.englishmaster_be.domain.user.user.dto.res.UserRes;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FlashCardFeedbackFullRes {
    private UUID id;
    private Integer star;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private FlashCardRes flashCard;
    private UserRes userFeedback;
}

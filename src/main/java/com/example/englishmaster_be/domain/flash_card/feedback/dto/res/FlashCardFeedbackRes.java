package com.example.englishmaster_be.domain.flash_card.feedback.dto.res;

import com.example.englishmaster_be.domain.user.dto.response.UserRes;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FlashCardFeedbackRes {
    private UUID id;
    private Integer star;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID flashCardId;
    private UUID userFeedbackId;
    private UserRes userFeedback;
}

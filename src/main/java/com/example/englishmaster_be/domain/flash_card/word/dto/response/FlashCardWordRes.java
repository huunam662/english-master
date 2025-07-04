package com.example.englishmaster_be.domain.flash_card.word.dto.response;

import com.example.englishmaster_be.domain.flash_card.flash_card.dto.response.FlashCardRes;
import com.example.englishmaster_be.domain.user.dto.response.UserRes;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class FlashCardWordRes {
    private UUID id;
    private String word;
    private String meaning;
    private String image;
    private String wordType;
    private String pronunciation;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private UUID flashCardId;
    private UUID createById;
    private FlashCardRes flashCard;
    private UserRes createBy;
}

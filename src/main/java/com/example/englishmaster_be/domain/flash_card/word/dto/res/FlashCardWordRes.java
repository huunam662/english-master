package com.example.englishmaster_be.domain.flash_card.word.dto.res;

import com.example.englishmaster_be.domain.flash_card.flash_card.dto.res.FlashCardRes;
import com.example.englishmaster_be.domain.user.user.dto.res.UserRes;
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
    private FlashCardRes flashCard;
    private UserRes createBy;
    private UserRes updateBy;
}

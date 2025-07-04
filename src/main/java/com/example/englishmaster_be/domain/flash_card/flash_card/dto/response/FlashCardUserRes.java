package com.example.englishmaster_be.domain.flash_card.flash_card.dto.response;

import com.example.englishmaster_be.domain.user.dto.response.UserRes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FlashCardUserRes extends FlashCardRes{
    UserRes createBy;
}

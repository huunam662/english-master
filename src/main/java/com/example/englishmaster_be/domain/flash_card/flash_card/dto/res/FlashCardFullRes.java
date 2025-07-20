package com.example.englishmaster_be.domain.flash_card.flash_card.dto.res;

import com.example.englishmaster_be.domain.user.user.dto.res.UserRes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FlashCardFullRes extends FlashCardRes{

    private UserRes createBy;
    private UserRes updateBy;

}

package com.example.englishmaster_be.domain.flash_card.dto.response;

import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlashCardUserResponse extends FlashCardResponse{

    UserBasicResponse flashCardOwner;
}

package com.example.englishmaster_be.domain.flash_card.word.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class UpdateFlashCardWordReq extends FlashCardWordReq{
    @NotNull(message = "Flash card word id is not null.")
    private UUID id;
}

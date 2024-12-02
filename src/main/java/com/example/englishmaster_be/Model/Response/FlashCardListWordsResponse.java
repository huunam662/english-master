package com.example.englishmaster_be.Model.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlashCardListWordsResponse {

    String flashCardTitle;

    String flashCardDescription;

    List<FlashCardWordResponse> flashCardWord;

}

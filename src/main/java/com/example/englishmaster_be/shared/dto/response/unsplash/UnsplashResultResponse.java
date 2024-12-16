package com.example.englishmaster_be.shared.dto.response.unsplash;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnsplashResultResponse {

    String urls;

}

package com.example.englishmaster_be.domain.part.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartSaveContentRequest {

    String contentType;

	String contentData;

}

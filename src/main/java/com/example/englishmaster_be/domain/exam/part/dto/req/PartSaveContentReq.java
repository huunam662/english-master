package com.example.englishmaster_be.domain.exam.part.dto.req;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
public class PartSaveContentReq {

    private String contentType;

	private String contentData;

}

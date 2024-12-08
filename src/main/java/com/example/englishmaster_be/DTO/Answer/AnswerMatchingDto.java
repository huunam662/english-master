package com.example.englishmaster_be.DTO.Answer;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerMatchingDto {
    private String contentLeft;
    private String contentRight;
}

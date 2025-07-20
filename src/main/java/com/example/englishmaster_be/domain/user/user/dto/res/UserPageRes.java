package com.example.englishmaster_be.domain.user.user.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserPageRes {

    private UserRoleRes user;
    private Long countMockTests;
    private Long countFlashCards;
    private Long countNews;
    private Long countComments;
}

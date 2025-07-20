package com.example.englishmaster_be.domain.news.news.dto.res;

import com.example.englishmaster_be.domain.user.user.dto.res.UserRes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class NewsFullRes extends NewsRes{
    private UserRes userCreate;
    private UserRes userUpdate;
}

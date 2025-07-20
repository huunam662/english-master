package com.example.englishmaster_be.domain.exam.topic.type.dto.res;

import com.example.englishmaster_be.domain.user.user.dto.res.UserRes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class TopicTypeFullRes extends TopicTypeRes{

    private UserRes userCreate;

    private UserRes userUpdate;
}

package com.example.englishmaster_be.domain.exam.topic.topic.dto.res;

import com.example.englishmaster_be.domain.exam.pack.pack.dto.res.PackAndTypeRes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class TopicPackTypeRes extends TopicAndTypeRes{

    PackAndTypeRes pack;

}

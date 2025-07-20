package com.example.englishmaster_be.domain.exam.topic.topic.dto.res;

import com.example.englishmaster_be.domain.exam.pack.pack.dto.res.PackAndTypeRes;
import com.example.englishmaster_be.domain.exam.part.dto.res.PartBasicRes;
import com.example.englishmaster_be.domain.exam.topic.type.dto.res.TopicTypeRes;
import com.example.englishmaster_be.domain.user.user.dto.res.UserRes;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TopicFullRes {

    private UUID topicId;
    private String topicName;
    private String topicImage;
    private String topicAudio;
    private String topicDescription;
    private LocalTime workTime;
    private Integer numberQuestion;
    private Boolean enable;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private UserRes userCreate;
    private UserRes userUpdate;
    private List<PartBasicRes> parts;
    private PackAndTypeRes pack;
    private TopicTypeRes topicType;

}

package com.example.englishmaster_be.domain.exam.part.dto.res;

import com.example.englishmaster_be.domain.user.user.dto.res.UserBasicRes;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
public class PartRes {

    private UUID partId;

    private String partName;

    private String partDescription;

    private String partType;

    private String contentType;

    private String contentData;

    
    private LocalDateTime createAt;

    
    private LocalDateTime updateAt;

    private UserBasicRes userCreate;

    private UserBasicRes userUpdate;

}

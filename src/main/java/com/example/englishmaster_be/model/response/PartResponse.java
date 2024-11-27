package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.Part;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartResponse {

    UUID partId;

    String partName;

    String partDescription;

    String partType;

    String contentType;

    String contentData;

    String createAt;

    String updateAt;

    int totalQuestion;


    public PartResponse(Part part) {

        if(Objects.isNull(part)) return;

        this.contentData = part.getContentData();
        this.partId = part.getPartId();
        this.partName = part.getPartName();
        this.partDescription = part.getPartDescription();
        this.partType = part.getPartType();
        this.contentType = part.getContentType();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        if(Objects.nonNull(part.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(part.getCreateAt()));
        if(Objects.nonNull(part.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(part.getUpdateAt()));
    }

    public PartResponse(Part part, int totalQuestion) {

        if(Objects.isNull(part)) return;

        this.contentData = part.getContentData();
        this.partId = part.getPartId();
        this.partName = part.getPartName();
        this.partDescription = part.getPartDescription();
        this.partType = part.getPartType();
        this.contentType = part.getContentType();
        this.totalQuestion = totalQuestion;

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        if(Objects.nonNull(part.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(part.getCreateAt()));
        if(Objects.nonNull(part.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(part.getUpdateAt()));
    }

}

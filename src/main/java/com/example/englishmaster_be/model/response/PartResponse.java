package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.Part;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Getter
@Setter
public class PartResponse {
    private UUID partId;
    private String partName;
    private String partDescription;
    private String partType;
    private String contentType;
    private String contentData;
    private String createAt;
    private String updateAt;
    private int totalQuestion;


    public PartResponse(Part part) {
        this.contentData = part.getContentData();
        this.partId = part.getPartId();
        this.partName = part.getPartName();
        this.partDescription = part.getPartDescription();
        this.partType = part.getPartType();
        this.contentType = part.getContentType();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(part.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(part.getUpdateAt()));
    }

    public PartResponse(Part part, int totalQuestion) {
        this.contentData = part.getContentData();
        this.partId = part.getPartId();
        this.partName = part.getPartName();
        this.partDescription = part.getPartDescription();
        this.partType = part.getPartType();
        this.contentType = part.getContentType();
        this.totalQuestion = totalQuestion;

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(part.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(part.getUpdateAt()));
    }

}

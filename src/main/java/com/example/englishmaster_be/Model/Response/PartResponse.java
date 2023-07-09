package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Component.GetExtension;
import com.example.englishmaster_be.Component.PublicLink;
import com.example.englishmaster_be.Model.Part;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;
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
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public PartResponse(Part part) {
        String link;
        if(part.getContentData() == null){
            this.contentData = part.getContentData();

        }else {

            link = GetExtension.linkName(part.getContentData());
            this.contentData = link + part.getContentData();
        }

        this.partId = part.getPartId();
        this.partName = part.getPartName();
        this.partDescription = part.getPartDescription();
        this.partType = part.getPartType();
        this.contentType = part.getContentType();
        this.createAt = part.getCreateAt();
        this.updateAt = part.getUpdateAt();

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", part.getUserCreate().getUserId());
        userCreate.put("User Name", part.getUserCreate().getName());

        userUpdate.put("User Id", part.getUserUpdate().getUserId());
        userUpdate.put("User Name", part.getUserUpdate().getName());
    }
}

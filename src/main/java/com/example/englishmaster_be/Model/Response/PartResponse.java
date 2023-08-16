package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Component.GetExtension;
import com.example.englishmaster_be.Model.Part;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

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

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(part.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(part.getUpdateAt()));
    }

	public PartResponse(Part part, int totalQuestion) {
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
		this.totalQuestion = totalQuestion;

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		this.createAt = sdf.format(Timestamp.valueOf(part.getCreateAt()));
		this.updateAt = sdf.format(Timestamp.valueOf(part.getUpdateAt()));
	}


	public UUID getPartId() {
		return partId;
	}

	public void setPartId(UUID partId) {
		this.partId = partId;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public String getPartDescription() {
		return partDescription;
	}

	public void setPartDescription(String partDescription) {
		this.partDescription = partDescription;
	}

	public String getPartType() {
		return partType;
	}

	public void setPartType(String partType) {
		this.partType = partType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentData() {
		return contentData;
	}

	public void setContentData(String contentData) {
		this.contentData = contentData;
	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}

	public String getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(String updateAt) {
		this.updateAt = updateAt;
	}

	public int getTotalQuestion() {
		return totalQuestion;
	}

	public void setTotalQuestion(int totalQuestion) {
		this.totalQuestion = totalQuestion;
	}
}

package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Model.*;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TopicResponse {
    private UUID topicId;
	private UUID packId;
	private String packName;

    private String topicName;

    private String topicImage;

    private String topicDescription;

    private String topicType;

	private List<UUID> listPart;

	private int numberQuestion;

    private String workTime;

    private String startTime;

    private String endTime;

    private String createAt;

    private String updateAt;

	private boolean isEnable;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public TopicResponse(Topic topic) {

        String link = GetExtension.linkName(topic.getTopicImage());

        this.topicId = topic.getTopicId();
		this.packId = topic.getPack().getPackId();
		this.packName = topic.getPack().getPackName();
        this.topicName = topic.getTopicName();

        this.topicImage =  link + topic.getTopicImage();
        this.topicDescription = topic.getTopicDescription();
        this.topicType = topic.getTopicType();
		this.numberQuestion = topic.getNumberQuestion();
        this.workTime = topic.getWorkTime();
		this.isEnable = topic.isEnable();

		List<UUID> listPart = new ArrayList<>();
		for(Part part : topic.getParts()){
			listPart.add(part.getPartId());
		}
		this.listPart = listPart;

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        this.startTime = sdf.format(Timestamp.valueOf(topic.getStartTime()));
        this.endTime = sdf.format(Timestamp.valueOf(topic.getEndTime()));
        this.createAt = sdf.format(Timestamp.valueOf(topic.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(topic.getUpdateAt()));

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", topic.getUserCreate().getUserId());
        userCreate.put("User Name", topic.getUserCreate().getName());

        userUpdate.put("User Id", topic.getUserUpdate().getUserId());
        userUpdate.put("User Name", topic.getUserUpdate().getName());
    }

	public UUID getTopicId() {
		return topicId;
	}

	public void setTopicId(UUID topicId) {
		this.topicId = topicId;
	}

	public UUID getPackId() {
		return packId;
	}

	public void setPackId(UUID packId) {
		this.packId = packId;
	}

	public String getPackName() {
		return packName;
	}

	public List<UUID> getListPart() {
		return listPart;
	}

	public void setListPart(List<UUID> listPart) {
		this.listPart = listPart;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getTopicImage() {
		return topicImage;
	}

	public void setTopicImage(String topicImage) {
		this.topicImage = topicImage;
	}

	public String getTopicDescription() {
		return topicDescription;
	}

	public void setTopicDescription(String topicDescription) {
		this.topicDescription = topicDescription;
	}

	public String getTopicType() {
		return topicType;
	}

	public void setTopicType(String topicType) {
		this.topicType = topicType;
	}

	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
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

	public JSONObject getUserCreate() {
		return userCreate;
	}

	public void setUserCreate(JSONObject userCreate) {
		this.userCreate = userCreate;
	}

	public JSONObject getUserUpdate() {
		return userUpdate;
	}

	public void setUserUpdate(JSONObject userUpdate) {
		this.userUpdate = userUpdate;
	}

	public int getNumberQuestion() {
		return numberQuestion;
	}

	public void setNumberQuestion(int numberQuestion) {
		this.numberQuestion = numberQuestion;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean enable) {
		isEnable = enable;
	}
}

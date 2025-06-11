package com.example.englishmaster_be.domain.excel.service.exp;

import com.example.englishmaster_be.common.constant.TopicType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

public interface IExcelExportService {

    ByteArrayInputStream exportTopicByType(TopicType topicType);

    ByteArrayInputStream exportAllTopic();

    ByteArrayInputStream exportTopicById(UUID topicId);
}

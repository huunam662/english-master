package com.example.englishmaster_be.domain.topic.repository.jdbc;

import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicJdbcRepository {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional
    public void insertTopic(TopicEntity topic) {

        if(topic == null) return;

        String sql = """
                    INSERT INTO topics(
                        id, topic_name, topic_description, create_at, update_at, enable,
                        create_by, update_by, topic_image, work_time, number_question,
                        pack_id, topic_type_id
                    )
                    VALUES(
                        :id, :topicName, :topicDescription, now(), now(), :enable,
                        :createBy, :updateBy, :topicImage, :workTime, :numberQuestion,
                        :packId, :topicTypeId
                    )
                    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", topic.getTopicId())
                .addValue("topicName", topic.getTopicName())
                .addValue("topicDescription", topic.getTopicDescription())
                .addValue("enable", topic.getEnable())
                .addValue("createBy", topic.getUserCreate().getUserId())
                .addValue("updateBy", topic.getUserCreate().getUserId())
                .addValue("topicImage", topic.getTopicImage())
                .addValue("workTime", Time.valueOf(topic.getWorkTime()))
                .addValue("numberQuestion", topic.getNumberQuestion())
                .addValue("packId", topic.getPack() != null ? topic.getPack().getPackId() : topic.getPackId())
                .addValue("topicTypeId", topic.getTopicType() != null ? topic.getTopicType().getTopicTypeId() : topic.getTopicTypeId());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Transactional
    public void updateTopic(UUID topicId, int numberQuestion){

        if(topicId == null) return;
        if(numberQuestion < 0) return;

        String sql = """
                UPDATE topics
                SET number_question = :numberQuestion
                WHERE id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", topicId)
                .addValue("numberQuestion", numberQuestion);

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Transactional
    public void updateTopic(UUID topicId, String imageUrl){
        if(topicId == null) return;
        if(imageUrl == null || imageUrl.isEmpty()) return;

        String sql = """
                UPDATE topics
                SET topic_image = :imageUrl
                WHERE id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", topicId)
                .addValue("imageUrl", imageUrl);

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Transactional
    public void updateTopic(
            UUID topicId,
            UUID packId,
            UUID topicTypeId,
            UUID userUpdateId,
            String topicName,
            String topicImage,
            String topicDescription,
            LocalTime workTime
    ){
        if(topicId == null || packId == null || topicTypeId == null || userUpdateId == null) return;
        String sql = """
                UPDATE topics
                SET pack_id = :packId,
                    topic_type_id = :topicTypeId,
                    update_by = :userUpdateId,
                    update_at = now(),
                    topic_name = :topicName,
                    topic_description = :topicDescription,
                    topic_image = :topicImage,
                    work_time = :workTime
                WHERE id = :topicId
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("topicId", topicId)
                .addValue("packId", packId)
                .addValue("topicTypeId", topicTypeId)
                .addValue("userUpdateId", userUpdateId)
                .addValue("topicName", topicName)
                .addValue("topicImage", topicImage)
                .addValue("topicDescription", topicDescription)
                .addValue("workTime", Time.valueOf(workTime));
        namedParameterJdbcTemplate.update(sql, params);
    }

}

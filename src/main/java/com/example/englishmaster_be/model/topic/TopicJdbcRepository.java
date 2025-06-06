package com.example.englishmaster_be.model.topic;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
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
                        create_by, update_by, topic_image, work_time, number_question, pack_id
                    )
                    VALUES(
                        :id, :topicName, :topicDescription, now(), now(), :enable,
                        :createBy, :updateBy, :topicImage, :workTime, :numberQuestion, :packId
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
                .addValue("packId", topic.getPack() != null ? topic.getPack().getPackId() : topic.getPackId());

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

}

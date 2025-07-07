package com.example.englishmaster_be.domain.topic_type.repository.jdbc;

import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicTypeJdbcRepository {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Transactional
    public UUID insertTopicType(TopicTypeEntity topicType){

        if(topicType == null) return null;

        String sql = """
                    INSERT INTO topic_type(
                        id, type_name, create_at, update_at, create_by, update_by
                    )
                    VALUES(:topicTypeId, :topicTypeName, now(), now(), :createBy, :updateBy)
                    ON CONFLICT (type_name)
                    DO UPDATE SET type_name = :topicTypeName
                    RETURNING id
                    """;
        topicType.setTopicTypeId(UUID.randomUUID());
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("topicTypeId", topicType.getTopicTypeId())
                .addValue("topicTypeName", topicType.getTopicTypeName())
                .addValue("createBy", topicType.getUserCreate().getUserId())
                .addValue("updateBy", topicType.getUserCreate().getUserId());

        return namedParameterJdbcTemplate.queryForObject(sql, params, UUID.class);
    }
}

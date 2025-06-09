package com.example.englishmaster_be.domain.part.repository.jdbc;

import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.value.AppValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartJdbcRepository {

    JdbcTemplate jdbcTemplate;

    AppValue appValue;

    @Transactional
    public void batchInsertPart(List<PartEntity> parts){

        if(parts == null || parts.isEmpty()) return;

        String sql = """
                    INSERT INTO part(
                        id, part_name, part_type, part_description,
                        create_at, update_at, create_by, update_by, topic_id
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        int partsSize = parts.size();
        int batchSize = appValue.getBatchSize();
        int startIndex = 0;

        while(startIndex < partsSize){

            int endIndex = startIndex + batchSize;

            if(endIndex > partsSize)
                endIndex = partsSize;

            List<PartEntity> partsSub = parts.subList(startIndex, endIndex);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    PartEntity part = partsSub.get(i);
                    ps.setObject(1, part.getPartId());
                    ps.setString(2, part.getPartName());
                    ps.setString(3, part.getPartType());
                    ps.setString(4, String.format("%s: %s", part.getPartName(), part.getPartType()));
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setObject(7, part.getUserCreate().getUserId());
                    ps.setObject(8, part.getUserCreate().getUserId());
                    ps.setObject(9, part.getTopic() != null ? part.getTopic().getTopicId() : part.getTopicId());
                }

                @Override
                public int getBatchSize() {
                    return partsSub.size();
                }
            });

            startIndex = endIndex;
        }
    }

}

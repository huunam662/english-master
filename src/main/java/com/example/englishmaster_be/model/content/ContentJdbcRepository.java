package com.example.englishmaster_be.model.content;

import com.example.englishmaster_be.value.AppValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentJdbcRepository {

    JdbcTemplate jdbcTemplate;

    AppValue appValue;

    @Transactional
    public void batchInsertContent(List<ContentEntity> contents){

        if(contents == null || contents.isEmpty()) return;

        String sql = """
                    INSERT INTO content(
                        id, create_at, update_at, create_by, update_by,
                        code, content_data, content_type, topic_id
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        int contentsSize = contents.size();
        int batchSize = appValue.getBatchSize();
        int startIndex = 0;

        while (startIndex < contentsSize) {

            int endIndex = startIndex + batchSize;

            if(endIndex > contentsSize)
                endIndex = contentsSize;

            List<ContentEntity> contentsSub = contents.subList(startIndex, endIndex);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ContentEntity content = contentsSub.get(i);
                    ps.setObject(1, content.getContentId());
                    ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setObject(4, content.getUserCreate().getUserId());
                    ps.setObject(5, content.getUserCreate().getUserId());
                    ps.setString(6, content.getCode());
                    ps.setString(7, content.getContentData());
                    ps.setString(8, content.getContentType());
                    ps.setObject(9, content.getTopic() != null ? content.getTopic().getTopicId() : null);
                }

                @Override
                public int getBatchSize() {

                    return contentsSub.size();
                }
            });

            startIndex = endIndex;
        }
    }
}

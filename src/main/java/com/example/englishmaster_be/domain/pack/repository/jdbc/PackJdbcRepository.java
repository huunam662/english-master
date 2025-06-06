package com.example.englishmaster_be.domain.pack.repository.jdbc;

import com.example.englishmaster_be.domain.pack.model.PackEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackJdbcRepository {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional
    public void insertPack(PackEntity pack){

        if(pack == null) return;

        String sql = """
                    INSERT INTO pack_topic(
                        id, pack_name, create_at, update_at,
                        create_by, update_by, pack_type_id
                    )
                    VALUES(
                        :id, :packName, now(), now(), :createBy, :updateBy, :packTypeId
                    )
                    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", pack.getPackId())
                .addValue("packName", pack.getPackName())
                .addValue("createBy", pack.getUserCreate().getUserId())
                .addValue("updateBy", pack.getUserCreate().getUserId())
                .addValue("packTypeId", pack.getPackType() != null ? pack.getPackType().getId() : pack.getPackTypeId());

        namedParameterJdbcTemplate.update(sql, params);
    }

}

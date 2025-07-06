package com.example.englishmaster_be.domain.pack_type.repository.jdbc;

import com.example.englishmaster_be.domain.pack_type.model.PackTypeEntity;
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
public class PackTypeJdbcRepository {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Transactional
    public UUID insertPackType(PackTypeEntity packType){

        if(packType==null) return null;

        String sql = """
                    INSERT INTO pack_type(
                        id, name, created_at, updated_at,
                        created_by, updated_by, description
                    ) 
                    VALUES(
                        :id, :name, now(), now(),
                        :createdBy, :updatedBy, :description
                    ) 
                    ON CONFLICT(LOWER(name))
                    DO UPDATE SET name = :name
                    RETURNING id;
                    """;

        packType.setId(UUID.randomUUID());
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", packType.getId())
                .addValue("name", packType.getName())
                .addValue("createdBy", packType.getCreatedBy().getUserId())
                .addValue("updatedBy", packType.getCreatedBy().getUserId())
                .addValue("description", packType.getDescription());

        return namedParameterJdbcTemplate.queryForObject(sql, params, UUID.class);
    }

}

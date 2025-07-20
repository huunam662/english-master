package com.example.englishmaster_be.domain.exam.pack.type.repository;

import com.example.englishmaster_be.domain.exam.pack.type.model.PackTypeEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public class PackTypeJdbcRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PackTypeJdbcRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

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
                    ON CONFLICT(name)
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

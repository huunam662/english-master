package com.example.englishmaster_be.domain.user.repository.jdbc;

import com.example.englishmaster_be.domain.user.model.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserJdbcRepository {

    NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    public void batchInsertUsers(List<UserEntity> users){
        if(users == null || users.isEmpty()) return;
        String sql = """
                    INSERT INTO users(
                        id, email, password, name, phone, role, is_enabled, avatar, address, create_at, update_at
                    )
                    VALUES(
                        :id, :email, :password, :name, :phone, :roleId, :isEnabled, :avatar, :address, now(), now()
                    )
                """;
        int usersSize = users.size();
        int batchSize = 100;
        int start = 0;
        while(start < usersSize){
            int end = start + batchSize;
            if(end > usersSize) end = usersSize;
            List<UserEntity> usersSub = users.subList(start, end);
            List<MapSqlParameterSource> params = usersSub.stream().map(
                    elm -> {
                        elm.setUserId(UUID.randomUUID());
                        elm.setCreateAt(LocalDateTime.now());
                        elm.setUpdateAt(LocalDateTime.now());
                        return new MapSqlParameterSource()
                                .addValue("id", elm.getUserId())
                                .addValue("email", elm.getEmail())
                                .addValue("password", elm.getPassword())
                                .addValue("name", elm.getName())
                                .addValue("phone", elm.getPhone())
                                .addValue("roleId", elm.getRole().getRoleId())
                                .addValue("isEnabled", elm.getEnabled())
                                .addValue("avatar", elm.getAvatar())
                                .addValue("address", elm.getAddress());
                    }
            ).toList();
            jdbcTemplate.batchUpdate(sql, params.toArray(MapSqlParameterSource[]::new));
            start = end;
        }
    }

}

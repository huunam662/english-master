package com.example.englishmaster_be.domain.flash_card.repository.jdbc;

import com.example.englishmaster_be.domain.flash_card.dto.request.FlashCardRequest;
import com.example.englishmaster_be.domain.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardJdbcRepository {

    NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    public void insertFlashCard(FlashCardEntity flashCard){
        if(flashCard == null) return;

        String sql = """
                    INSERT INTO flash_card(
                        id, title, description, image, create_at, update_at,
                        create_by, update_by, user_id
                    ) VALUES(
                        :flashCardId, :flashCardTitle, :flashCardDescription, :flashCardImage,
                        now(), now(), :createBy, :updateBy, :flashCardOwner
                    )
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("flashCardId", flashCard.getFlashCardId())
                .addValue("flashCardTitle", flashCard.getFlashCardTitle())
                .addValue("flashCardDescription", flashCard.getFlashCardDescription())
                .addValue("flashCardImage", flashCard.getFlashCardImage())
                .addValue("createBy", flashCard.getUserCreate().getUserId())
                .addValue("updateBy", flashCard.getUserCreate().getUserId())
                .addValue("flashCardOwner", flashCard.getFlashCardOwner() != null ? flashCard.getFlashCardOwner().getUserId() : null);

        jdbcTemplate.update(sql, params);
    }

    @Transactional
    public void updateFlashCard(UUID flashCardId, FlashCardRequest flashCardRequest, UserEntity user){

        if(flashCardId == null) return;
        if(flashCardRequest == null) return;

        String sql = """
                    UPDATE flash_card
                    SET title = :flashCardTitle,
                        description = :flashCardDescription,
                        image = :flashCardImage,
                        update_by = :updateBy
                    WHERE id = :flashCardId
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("flashCardId", flashCardId)
                .addValue("flashCardTitle", flashCardRequest.getFlashCardTitle())
                .addValue("flashCardDescription", flashCardRequest.getFlashCardDescription())
                .addValue("flashCardImage", flashCardRequest.getFlashCardImage())
                .addValue("updateBy", user.getUserId());

        jdbcTemplate.update(sql, params);
    }

}

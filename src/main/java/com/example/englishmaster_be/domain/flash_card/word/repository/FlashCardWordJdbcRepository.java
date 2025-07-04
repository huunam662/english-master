package com.example.englishmaster_be.domain.flash_card.word.repository;

import com.example.englishmaster_be.domain.flash_card.word.model.FlashCardWordEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.value.AppValue;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class FlashCardWordJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final AppValue appValue;
    private final IUserService userService;

    @Lazy
    public FlashCardWordJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate, AppValue appValue, IUserService userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.appValue = appValue;
        this.userService = userService;
    }

    @Transactional
    public void insertBatchFlashCardWords(List<FlashCardWordEntity> flashCardWords) {
        UserEntity userLogging = userService.currentUser();
        if(flashCardWords == null || flashCardWords.isEmpty()) return;
        String sql = """
                    INSERT INTO flash_card_word(
                        id, word, meaning, word_type, pronunciation, image, flash_card_id,
                        create_by, create_at, update_by, update_at
                    )
                    VALUES(
                        :id, :word, :meaning, :wordType, :pronunciation, :image,
                        :flashCardId, :createBy, :createAt, :updateBy, :updateAt
                    )
                """;
        int flashCardWordsSize = flashCardWords.size();
        int batchSize = appValue.getBatchSize();
        int start = 0;
        while (start < flashCardWordsSize){
            int end = Math.min(start + batchSize, flashCardWordsSize);
            List<FlashCardWordEntity> flashCardWordsSub = flashCardWords.subList(start, end);
            List<MapSqlParameterSource> params = flashCardWordsSub.stream().map(
                    elm -> {
                        elm.setId(UUID.randomUUID());
                        elm.setCreateAt(LocalDateTime.now());
                        elm.setUpdateAt(LocalDateTime.now());
                        elm.setCreateBy(userLogging);
                        elm.setUpdateBy(userLogging);
                        elm.setCreateById(userLogging.getUserId());
                        elm.setUpdateById(userLogging.getUserId());
                        return new MapSqlParameterSource()
                                .addValue("id", elm.getId())
                                .addValue("word", elm.getWord())
                                .addValue("meaning", elm.getMeaning())
                                .addValue("wordType", elm.getWordType())
                                .addValue("pronunciation", elm.getPronunciation())
                                .addValue("image", elm.getImage())
                                .addValue("flashCardId", elm.getFlashCard().getId())
                                .addValue("createBy", elm.getCreateBy().getUserId())
                                .addValue("updateBy", elm.getUpdateBy().getUserId())
                                .addValue("createAt", Timestamp.valueOf(elm.getCreateAt()))
                                .addValue("updateAt", Timestamp.valueOf(elm.getUpdateAt()));
                    }
            ).toList();
            jdbcTemplate.batchUpdate(sql, params.toArray(MapSqlParameterSource[]::new));
            start = end;
        }
    }

    @Transactional
    public void updateBatchFlashCardWords(List<FlashCardWordEntity> flashCardWords) {
        UserEntity userLogging = userService.currentUser();
        if(flashCardWords == null || flashCardWords.isEmpty()) return;
        String sql = """
                UPDATE flash_card_word
                SET word = :word,
                    meaning = :meaning,
                    word_type = :wordType,
                    pronunciation = :pronunciation,
                    image = :image,
                    flash_card_id = :flashCardId,
                    update_by = :updateBy,
                    update_at = :updateAt
                WHERE id = :id
                """;
        int flashCardWordsSize = flashCardWords.size();
        int batchSize = appValue.getBatchSize();
        int start = 0;
        while (start < flashCardWordsSize){
            int end = Math.min(start + batchSize, flashCardWordsSize);
            List<FlashCardWordEntity> flashCardWordsSub = flashCardWords.subList(start, end);
            List<MapSqlParameterSource> params = flashCardWordsSub.stream().map(
                    elm -> {
                        elm.setUpdateAt(LocalDateTime.now());
                        elm.setUpdateBy(userLogging);
                        elm.setUpdateById(userLogging.getUserId());
                        return new MapSqlParameterSource()
                                .addValue("id", elm.getId())
                                .addValue("word", elm.getWord())
                                .addValue("meaning", elm.getMeaning())
                                .addValue("wordType", elm.getWordType())
                                .addValue("pronunciation", elm.getPronunciation())
                                .addValue("image", elm.getImage())
                                .addValue("flashCardId", elm.getFlashCard().getId())
                                .addValue("updateBy", elm.getUpdateBy().getUserId())
                                .addValue("updateAt", Timestamp.valueOf(elm.getUpdateAt()));
                    }
            ).toList();
            jdbcTemplate.batchUpdate(sql, params.toArray(MapSqlParameterSource[]::new));
            start = end;
        }
    }

}

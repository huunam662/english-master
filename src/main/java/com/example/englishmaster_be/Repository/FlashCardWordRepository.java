package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FlashCardWordRepository extends JpaRepository<FlashCardWord, UUID> {
    @Query("SELECT f FROM FlashCardWord f WHERE LOWER(f.word) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<FlashCardWord> findFlashCartWordByQuery(Pageable pageable, @Param("query") String query);
}


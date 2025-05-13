package com.example.englishmaster_be.model.news;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface NewsRepository extends JpaRepository<NewsEntity, UUID> {

    Optional<NewsEntity> findByNewsId(UUID newsId);

    @Query("SELECT n FROM NewsEntity n WHERE n.title = :title")
    Optional<NewsEntity> findByTitle(@Param("title") String title);
}

package com.example.englishmaster_be.domain.news.news.repository;

import com.example.englishmaster_be.domain.news.news.model.NewsEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepository extends JpaRepository<NewsEntity, UUID> {

    Optional<NewsEntity> findByNewsId(UUID newsId);

    @Query("SELECT n FROM NewsEntity n WHERE n.title = :title")
    Optional<NewsEntity> findByTitle(@Param("title") String title);

    @Query("""
        SELECT n FROM NewsEntity n
        WHERE LOWER(n.title) LIKE CONCAT('%', LOWER(:title), '%')
        ORDER BY n.updateAt DESC
    """)
    List<NewsEntity> findAllEntityByTitle(@Param("title") String title, Pageable pageable);
}

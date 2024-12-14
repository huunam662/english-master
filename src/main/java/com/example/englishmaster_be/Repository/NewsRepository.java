package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.entity.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepository extends JpaRepository<NewsEntity, UUID> {
    Optional<NewsEntity> findByNewsId(UUID newsId);
}

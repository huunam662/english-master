package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContentRepository extends JpaRepository<Content, UUID> {
    Optional<Content> findByContentId(UUID contentUId);

    @Query("select c.contentData from Content c order by c.contentId")
    List<String> findAllContentData();

    @Query("select c.contentData from Content c where c.topicId = :topicId and c.code like :contentAudio")
    String findContentDataByTopicIdAndCode(UUID topicId, String contentAudio);

    @Query("select c from Content c where c.contentData like :contentImage")
    Optional<Content> findByContentData(String contentImage);

}

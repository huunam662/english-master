package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContentRepository extends JpaRepository<ContentEntity, UUID> {
    Optional<ContentEntity> findByContentId(UUID contentUId);

    @Query("select c.contentData from ContentEntity c order by c.contentId")
    List<String> findAllContentData();

    @Query("select c.contentData from ContentEntity c where c.topicId = :topicId and c.code = :contentAudio")
    String findContentDataByTopicIdAndCode(UUID topicId, String contentAudio);

    @Query("SELECT c FROM ContentEntity c WHERE c.topicId = :topicId AND c.code = :contentAudio")
    Optional<ContentEntity> findContentByTopicIdAndCode(UUID topicId, String contentAudio);

    @Query("select c from ContentEntity c where c.contentData like :contentImage")
    Optional<ContentEntity> findByContentData(String contentImage);

    @Modifying
    @Query("delete from ContentEntity c where c.contentData = :contentData")
    int deleteByContentData(String contentData);

}

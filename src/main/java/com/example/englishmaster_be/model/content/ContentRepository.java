package com.example.englishmaster_be.model.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContentRepository extends JpaRepository<ContentEntity, UUID> {
    Optional<ContentEntity> findByContentId(UUID contentUId);

    @Query("select c.contentData from ContentEntity c order by c.contentId")
    List<String> findAllContentData();

    @Query("select c.contentData from ContentEntity c where c.topic.topicId = :topicId and c.code = :contentAudio")
    String findContentDataByTopicIdAndCode(UUID topicId, String contentAudio);

    @Query("SELECT c FROM ContentEntity c WHERE c.topic.topicId = :topicId AND c.code = :contentAudio")
    Optional<ContentEntity> findContentByTopicIdAndCode(UUID topicId, String contentAudio);

    @Query("select c from ContentEntity c where c.contentData = :contentData")
    Optional<ContentEntity> findByContentData(String contentData);

    @Modifying
    @Query("delete from ContentEntity c where c.contentData = :contentData")
    int deleteByContentData(String contentData);

    @Query(value = """
        SELECT EXISTS(
            SELECT c.id FROM content c
            WHERE c.content_data = :contentData
        )
    """, nativeQuery = true)
    boolean isExistsByContentData(@Param("contentData") String contentData);
}

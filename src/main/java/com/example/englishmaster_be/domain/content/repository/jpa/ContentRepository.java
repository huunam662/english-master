package com.example.englishmaster_be.domain.content.repository.jpa;

import com.example.englishmaster_be.domain.content.model.ContentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("select c from ContentEntity c where c.contentData = :contentData")
    Page<ContentEntity> findByContentData(String contentData, Pageable pageable);

    @Modifying
    @Query("delete from ContentEntity c where c.contentData = :contentData")
    int deleteByContentData(String contentData);

    @Query(value = """
        SELECT EXISTS(
            SELECT DISTINCT c.id FROM content c
            WHERE c.content_data = :contentData
        )
    """, nativeQuery = true)
    boolean isExistsByContentData(@Param("contentData") String contentData);

    @Query(value = """
        SELECT c.content_data FROM content c
        WHERE c.content_data IN :contentDatas
    """, nativeQuery = true)
    List<String> findAllContentDataIn(@Param("contentDatas") List<String> contentDatas);
}

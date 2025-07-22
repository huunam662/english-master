package com.example.englishmaster_be.domain.exam.part.repository;

import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartRepository extends JpaRepository<PartEntity, UUID>, JpaSpecificationExecutor<PartEntity> {

    Optional<PartEntity> findByPartId(UUID partID);

    @Query("SELECT DISTINCT p FROM PartEntity p JOIN FETCH p.topic t WHERE t.topicId =:topicId ORDER BY p.partName ASC")
    Page<PartEntity> findByTopics(UUID topicId, Pageable pageable);

    @Query("SELECT p FROM PartEntity p WHERE LOWER(p.partName) = LOWER(:partName)")
    Optional<PartEntity> findByPartName(@Param("partName") String partName);

    @Query("SELECT EXISTS(SELECT p FROM PartEntity p WHERE p != :part AND LOWER(p.partName) = LOWER(:partName))")
    boolean isExistedPartNameWithDiff(@Param("part") PartEntity part, @Param("partName") String partName);


    @Query(value = """
        SELECT EXISTS(SELECT p.id FROM part p WHERE p.id = :partId)
    """, nativeQuery = true)
    boolean isExistedByPartId(@Param("partId") UUID partId);

    @Query("""
        SELECT DISTINCT p FROM PartEntity p
        INNER JOIN FETCH p.topic t
        LEFT JOIN FETCH p.questions qgc
        LEFT JOIN FETCH qgc.answers aqc
        WHERE t.topicId = :topicId
        AND qgc.questionGroupParent != NULL
        AND aqc.answerId IN :answerIds
    """)
    List<PartEntity> findPartJoinQuestionsAndAnswers(@Param("topicId") UUID topicId, @Param("answerIds") List<UUID> answerIds);

    @Query("""
        SELECT p FROM PartEntity p
        INNER JOIN p.topic t
        WHERE LOWER(p.partName) = LOWER(:partName)
        AND t.topicId = :topicId
    """)
    Optional<PartEntity> findPartByPartNameTopicId(@Param("partName") String partName, @Param("topicId") UUID topicId);

    @Query("""
        SELECT DISTINCT p FROM PartEntity p
        LEFT JOIN FETCH p.questions qp
        LEFT JOIN FETCH qp.questionGroupChildren qc
        LEFT JOIN FETCH qc.answers ac
        WHERE p.partId = :partId
        AND qp.isQuestionParent = TRUE
        AND qc.isQuestionParent = FALSE
    """)
    Optional<PartEntity> findPartJoinQuestionAnswer(@Param("partId") UUID partId);

    @Query(value = """
        SELECT part_name FROM part
        WHERE topic_id = :topicId
        AND part_name IN :partNames
    """, nativeQuery = true)
    List<String> findPartNamesByTopicIdAndIn(@Param("topicId") UUID topicId, @Param("partNames") List<String> partNames);

    @Query(value = """
        SELECT id FROM part
        WHERE topic_id = :topicId
    """, nativeQuery = true)
    List<UUID> findPartIdsByTopicId(@Param("topicId") UUID topicId);

    @Query(value = """
        SELECT id FROM part
        WHERE topic_id = :topicId
        AND LOWER(part_name) = LOWER(:partName)
    """, nativeQuery = true)
    UUID findPartIdByTopicIdAndPartName(@Param("topicId") UUID topicId, @Param("partName") String partName);

    @Query("""
        SELECT p FROM PartEntity p
        WHERE p.topicId IN :topicIds
    """)
    List<PartEntity> findPartsByTopicIds(@Param("topicIds") List<UUID> topicIds);
}

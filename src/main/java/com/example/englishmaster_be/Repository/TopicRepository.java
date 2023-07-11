package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, UUID> {
    Page<Topic> findAll(Pageable pageable);

    Optional<Topic> findByTopicId(UUID topicId);

}

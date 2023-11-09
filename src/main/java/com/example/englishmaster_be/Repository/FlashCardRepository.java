package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface FlashCardRepository extends JpaRepository<FlashCard, UUID> {
    Optional<FlashCard> findByFlashCardId(UUID flashCardID);

    List<FlashCard> findByUser(User user, Sort sort);
}

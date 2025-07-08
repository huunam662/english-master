package com.example.englishmaster_be.domain.flash_card.feedback.model;

import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "flash_card_feedback")
@Entity
@Data
@Schema(hidden = true)
@EntityListeners(AuditingEntityListener.class)
public class FlashCardFeedbackEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "star")
    private Integer star;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "flash_card_id", insertable = false, updatable = false)
    private UUID flashCardId;

    @Column(name = "user_feedback_id", insertable = false, updatable = false)
    private UUID userFeedbackId;

    @JoinColumn(name = "flash_card_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private FlashCardEntity flashCard;

    @JoinColumn(name = "user_feedback_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @CreatedBy
    private UserEntity userFeedback;

}

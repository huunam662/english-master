package com.example.englishmaster_be.domain.flash_card.word.model;

import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "flash_card_word")
@Getter
@Setter
@NoArgsConstructor
@Schema(hidden = true)
@EntityListeners(AuditingEntityListener.class)
public class FlashCardWordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "word")
    private String word;

    @Column(name = "meaning")
    private String meaning;

    @Column(name = "image")
    private String image;

    @Column(name = "word_type")
    private String wordType;

    @Column(name = "pronunciation")
    private String pronunciation;

    @Column(name = "create_at")
    @CreatedDate
    private LocalDateTime createAt;

    @Column(name = "update_at")
    @LastModifiedDate
    private LocalDateTime updateAt;

    @Column(name = "create_by", insertable = false, updatable = false)
    private UUID createById;

    @Column(name = "update_by", insertable = false, updatable = false)
    private UUID updateById;

    @Column(name = "flash_card_id", insertable = false, updatable = false)
    private UUID flashCardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_card_id", referencedColumnName = "id")
    private FlashCardEntity flashCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    @CreatedBy
    private UserEntity createBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    @LastModifiedBy
    private UserEntity updateBy;

}

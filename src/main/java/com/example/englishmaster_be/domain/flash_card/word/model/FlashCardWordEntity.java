package com.example.englishmaster_be.domain.flash_card.word.model;

import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "flash_card_word")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlashCardWordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID id;

    @Column(name = "word")
    String word;

    @Column(name = "meaning")
    String meaning;

    @Column(name = "image")
    String image;

    @Column(name = "word_type")
    String wordType;

    @Column(name = "pronunciation")
    String pronunciation;

    @Column(name = "create_at")
    @CreatedDate
    LocalDateTime createAt;

    @Column(name = "update_at")
    @LastModifiedDate
    LocalDateTime updateAt;

    @Column(name = "create_by", insertable = false, updatable = false)
    UUID createById;

    @Column(name = "update_by", insertable = false, updatable = false)
    UUID updateById;

    @Column(name = "flash_card_id", insertable = false, updatable = false)
    UUID flashCardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_card_id", referencedColumnName = "id")
    FlashCardEntity flashCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    @CreatedBy
    UserEntity createBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    @LastModifiedBy
    UserEntity updateBy;

}

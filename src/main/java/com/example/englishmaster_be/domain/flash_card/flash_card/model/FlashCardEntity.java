package com.example.englishmaster_be.domain.flash_card.flash_card.model;


import com.example.englishmaster_be.domain.flash_card.word.model.FlashCardWordEntity;
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
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "flash_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlashCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID id;

    @Column(name = "title")
    String title;

    @Column(name="image")
    String image;

    @Column(name = "description")
    String description;

    @Column(name = "public_shared")
    Boolean publicShared;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    @CreatedBy
    UserEntity createBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    @LastModifiedBy
    UserEntity updateBy;

    @OneToMany(mappedBy = "flashCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<FlashCardWordEntity> flashCardWords;
}

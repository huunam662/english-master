package com.example.englishmaster_be.domain.flash_card.flash_card.model;


import com.example.englishmaster_be.domain.flash_card.feedback.model.FlashCardFeedbackEntity;
import com.example.englishmaster_be.domain.flash_card.word.model.FlashCardWordEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "flash_card")
@Getter
@Setter
@NoArgsConstructor
@Schema(hidden = true)
@EntityListeners(AuditingEntityListener.class)
public class FlashCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name="image")
    private String image;

    @Column(name = "description")
    private String description;

    @Column(name = "public_shared")
    private Boolean publicShared;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    @CreatedBy
    private UserEntity createBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    @LastModifiedBy
    private UserEntity updateBy;

    @OneToMany(mappedBy = "flashCard", fetch = FetchType.LAZY)
    private Set<FlashCardFeedbackEntity> flashCardFeedbacks;

    @OneToMany(mappedBy = "flashCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FlashCardWordEntity> flashCardWords;
}

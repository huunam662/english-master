package com.example.englishmaster_be.domain.exam.pack.pack.model;

import com.example.englishmaster_be.domain.exam.pack.type.model.PackTypeEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "pack_topic")
@Getter
@Setter
@NoArgsConstructor
@Schema(hidden = true)
@EntityListeners(AuditingEntityListener.class)
public class PackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID packId;

    @Column(name = "pack_name")
    private String packName;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "pack_type_id", insertable = false, updatable = false)
    private UUID packTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    private UserEntity userCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    private UserEntity userUpdate;

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Set<TopicEntity> topics;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pack_type_id", referencedColumnName = "id")
    private PackTypeEntity packType;
    
}

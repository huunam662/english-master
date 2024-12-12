package com.example.englishmaster_be.entity;

import com.example.englishmaster_be.Common.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "Status")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID statusId;

    @Column(name = "status_name")
    @Enumerated(EnumType.STRING)
    StatusEnum statusName;

    @Column(name = "flag")
    Boolean flag;

    @ManyToOne
    @JoinColumn(name = "Type", referencedColumnName = "id")
    TypeEntity type;

    @OneToMany(mappedBy = "status")
    List<TopicEntity> topicList;

}
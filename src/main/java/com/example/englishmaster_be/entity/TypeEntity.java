package com.example.englishmaster_be.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "Type")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID typeId;

    @Column(name = "type_name")
    String typeName;

    @Column(name = "name_slug")
    String nameSlug;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    List<StatusEntity> statuses;

}
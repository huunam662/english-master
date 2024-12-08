package com.example.englishmaster_be.Model;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Hidden
@Entity
@Table(name = "Type")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID typeId;

    @Column(name = "name_slug")
    String nameSlug;

    @Column(name = "type_name")
    String typeName;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    List<Status> statuses;

}
package com.example.englishmaster_be.Model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "type")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    UUID typeId;

    @Column(name = "name_slug")
    String nameSlug;

    @Column(name = "type_name")
    String typeName;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    Collection<Status> statuses;
}
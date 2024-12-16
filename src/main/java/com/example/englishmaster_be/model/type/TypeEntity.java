package com.example.englishmaster_be.model.type;

import com.example.englishmaster_be.model.status.StatusEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
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

    @OneToMany(mappedBy = "type")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    List<StatusEntity> statuses;

}
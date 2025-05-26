package com.example.englishmaster_be.model.status;

import com.example.englishmaster_be.model.topic.TopicEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.Collection;
import java.util.List;
import java.util.Set;
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
    String statusName;

    @Column(name = "flag")
    Boolean flag;

    @OneToMany(mappedBy = "status", fetch = FetchType.LAZY)
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    Set<TopicEntity> topicList;

}
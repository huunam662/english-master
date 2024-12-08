package com.example.englishmaster_be.Model;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Hidden
@Entity
@Table(name = "Status")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Status implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID statusId;

    @Column(name = "status_name")
    String statusName;

    @Column(name = "flag")
    boolean flag;

    @ManyToOne
    @JoinColumn(name = "Type", referencedColumnName = "id")
    Type type;

    @OneToMany(mappedBy = "status")
    List<Topic> topicList;

}
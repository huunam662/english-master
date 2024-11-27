package com.example.englishmaster_be.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID roleId;

    @Column(name = "role_name")
    String roleName;

    @Column(name = "role_description")
    String roleDescription;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    List<User> users;


}

package com.example.englishmaster_be.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;


@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID roleId;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_discription")
    private String roleDiscription;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private Collection<User> users;

    public Role() {
    }

}

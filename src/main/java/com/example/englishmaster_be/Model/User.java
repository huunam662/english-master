package com.example.englishmaster_be.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID userId;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String address;
    private String avatar;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @ManyToOne
    @JoinColumn(name = "role", referencedColumnName = "id")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Collection<ConfirmationToken> confirmToken;

//    @OneToMany(mappedBy = "userCreate", cascade = CascadeType.ALL)
//    private Collection<Topic> topicCreate;
//
//    @OneToMany(mappedBy = "userUpdate", cascade = CascadeType.ALL)
//    private Collection<Topic> topicUpdate;

    public User() {
        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();

    }

}

package com.example.englishmaster_be.Model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID userId;

    String email;

    String password;

    String name;

    String phone;

    String address;

    String avatar;

    @Column(name = "is_enabled")
    boolean isEnabled;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role", referencedColumnName = "id")
    Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<ConfirmationToken> confirmToken;

    @OneToMany(mappedBy = "userComment", cascade = CascadeType.ALL)
    Collection<Comment> comments;

    @OneToMany(mappedBy = "userPost", cascade = CascadeType.ALL)
    List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<FlashCard> flashCards;

//    @OneToMany(mappedBy = "userCreate", cascade = CascadeType.ALL)
//    private Collection<Topic> topicCreate;
//
//    @OneToMany(mappedBy = "userUpdate", cascade = CascadeType.ALL)
//    private Collection<Topic> topicUpdate;



}

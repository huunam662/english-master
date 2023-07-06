package com.example.englishmaster_be.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name="user_confirm_token")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID userConfirmTokenId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "type")
    private String type;

    @Column(name = "code")
    private String code;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public ConfirmationToken() {
    }

    public ConfirmationToken(User user){
        this.user = user;
        createAt = LocalDateTime.now();
    }


}

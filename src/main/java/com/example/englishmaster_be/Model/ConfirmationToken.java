package com.example.englishmaster_be.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
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

    public UUID getUserConfirmTokenId() {
        return userConfirmTokenId;
    }

    public void setUserConfirmTokenId(UUID userConfirmTokenId) {
        this.userConfirmTokenId = userConfirmTokenId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

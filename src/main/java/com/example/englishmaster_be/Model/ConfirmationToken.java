package com.example.englishmaster_be.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="user_confirm_token")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_confirm_token_id", columnDefinition = "BINARY(16)")
    private UUID userConfirmTokenId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "confirm_expiry")
    private LocalDateTime confirmExpiry;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    public ConfirmationToken() {
    }

    public ConfirmationToken(User user){
        this.user = user;
        createAt = LocalDateTime.now();
        confirmExpiry = LocalDateTime.now().plusMinutes(5);
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

    public LocalDateTime getConfirmExpiry() {
        return confirmExpiry;
    }

    public void setConfirmExpiry(LocalDateTime confirmExpiry) {
        this.confirmExpiry = confirmExpiry;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

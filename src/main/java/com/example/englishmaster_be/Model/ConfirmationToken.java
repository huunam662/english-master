package com.example.englishmaster_be.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name="user_confirm_token")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
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

    public ConfirmationToken(User user){
        this.user = user;
        createAt = LocalDateTime.now();
	}
}

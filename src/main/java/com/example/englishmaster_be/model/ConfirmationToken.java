package com.example.englishmaster_be.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="user_confirm_token")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
    UUID userConfirmTokenId;

    @Column(name = "type")
    String type;

    @Column(name = "code")
    String code;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at")
    @CreationTimestamp
    LocalDateTime createAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    public ConfirmationToken(User user){

        this.user = user;
	}
}

package com.example.englishmaster_be.domain.user.user.model;

import com.example.englishmaster_be.common.constant.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@Schema(hidden = true)
@EntityListeners(AuditingEntityListener.class)
public class RoleEntity implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID roleId;

    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    private Role roleName;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Set<UserEntity> users;

    @Override
    @JsonIgnore
    public String getAuthority() {
        return String.format("ROLE_%s", this.getRoleName().name());
    }
}

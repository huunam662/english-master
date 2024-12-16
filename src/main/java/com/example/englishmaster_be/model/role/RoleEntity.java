package com.example.englishmaster_be.model.role;

import com.example.englishmaster_be.common.constant.RoleEnum;
import com.example.englishmaster_be.model.user.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID roleId;

    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    RoleEnum roleName;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    List<UserEntity> users;


}

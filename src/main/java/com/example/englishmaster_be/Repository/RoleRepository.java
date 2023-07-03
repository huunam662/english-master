package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role findByRoleId(UUID roleID);
}

package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    @Query("select r from Role r where r.roleName like %:roleName%")
    Role findByRoleName(String roleName);
}

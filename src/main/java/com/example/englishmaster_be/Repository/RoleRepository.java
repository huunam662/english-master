package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    @Query("select r from RoleEntity r where r.roleName = :roleName")
    RoleEntity findByRoleName(String roleName);
}

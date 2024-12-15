package com.example.englishmaster_be.model.role;

import com.example.englishmaster_be.common.constant.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    @Query("select r from RoleEntity r where r.roleName = :roleName")
    RoleEntity findByRoleName(RoleEnum roleName);
}

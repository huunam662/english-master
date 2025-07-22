package com.example.englishmaster_be.domain.user.user.repository;

import com.example.englishmaster_be.common.constant.Role;
import com.example.englishmaster_be.domain.user.user.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    @Query("select r from RoleEntity r where r.roleName = :roleName")
    RoleEntity findByRoleName(@Param("roleName") Role roleName);
}

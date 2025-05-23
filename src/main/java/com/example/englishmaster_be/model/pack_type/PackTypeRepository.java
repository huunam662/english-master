package com.example.englishmaster_be.model.pack_type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PackTypeRepository extends JpaRepository<PackTypeEntity, UUID> {

    @Query("SELECT pt FROM PackTypeEntity pt WHERE LOWER(pt.name) = :name")
    Optional<PackTypeEntity> findByName(@Param("name") String name);

    @Query("SELECT EXISTS(SELECT pt.name FROM PackTypeEntity pt WHERE LOWER(pt.name) = :name)")
    Boolean existsByName(@Param("name") String name);

}

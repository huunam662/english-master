package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.entity.TypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface TypeRepository extends JpaRepository<TypeEntity, UUID> {
}

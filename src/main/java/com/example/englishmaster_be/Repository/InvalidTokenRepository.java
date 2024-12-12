package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.entity.InvalidTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidTokenRepository extends JpaRepository<InvalidTokenEntity, String> {
}

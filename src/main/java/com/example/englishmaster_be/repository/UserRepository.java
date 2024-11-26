package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("UserRepository")
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByUserId(UUID userId);

    User findByEmail(String email);

    boolean existsByEmail(String email);

}

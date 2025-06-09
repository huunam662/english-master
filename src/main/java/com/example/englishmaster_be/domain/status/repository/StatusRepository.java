package com.example.englishmaster_be.domain.status.repository;

import com.example.englishmaster_be.domain.status.model.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StatusRepository extends JpaRepository<StatusEntity, UUID> {

    @Query("SELECT s FROM StatusEntity s WHERE s.statusName = :statusName")
    Optional<StatusEntity> findByStatusName(@Param("statusName") String statusName);


    @Query("SELECT EXISTS(SELECT s FROM StatusEntity s WHERE s.statusName = :statusName)")
    boolean isExistedByStatusNameOfType(@Param("statusName") String statusName);

}

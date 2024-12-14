package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Common.enums.StatusEnum;
import com.example.englishmaster_be.entity.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StatusRepository extends JpaRepository<StatusEntity, UUID> {

    @Query("SELECT s FROM StatusEntity s WHERE s.statusName = :statusName")
    Optional<StatusEntity> findByStatusName(StatusEnum statusName);


    @Query("SELECT EXISTS(SELECT s FROM StatusEntity s WHERE s != :status AND s.statusName = :statusName)")
    boolean isExistedByStatusNameWithDiff(@Param("status") StatusEntity status, @Param("statusName") StatusEnum statusName);

}

package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp,String> {

}

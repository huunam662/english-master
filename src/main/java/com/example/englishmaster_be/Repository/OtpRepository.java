package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface OtpRepository extends JpaRepository<Otp,String> {

}

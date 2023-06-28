package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Configuration.jwt.JwtUtils;
import com.example.englishmaster_be.DTO.UserLoginDTO;
import com.example.englishmaster_be.DTO.UserRegisterDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.UUID;


@Service
public class UserServiceImpl implements UserService{



    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;


    @Override
    public User createUser(UserRegisterDTO userRegisterDTO) {
        User user= new User();

        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setName(userRegisterDTO.getName());
        user.setRole(roleRepository.findByRoleId(UUID.fromString("876e0c22-7c28-47b1-abe8-589260227b07")));

        return user;
    }

    @Override
    public void changePassword(User user, String newpass) {
        user.setPassword(passwordEncoder.encode((newpass)));
    }

}

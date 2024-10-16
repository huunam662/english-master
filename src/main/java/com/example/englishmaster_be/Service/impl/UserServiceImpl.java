package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Constant.RoleConstant;
import com.example.englishmaster_be.DTO.UserRegisterDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;


    @Override
    public User createUser(UserRegisterDTO userRegisterDTO) {
        User user = new User();

        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setName(userRegisterDTO.getName());
        user.setRole(roleRepository.findByRoleName(RoleConstant.USER));

        return user;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public void changePassword(User user, String newpass) {
        user.setPassword(passwordEncoder.encode((newpass)));
    }

    @Override
    public User findUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername());
    }

    @Override
    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return findUser(userDetails);
        }
        return null;
    }

    @Override
    public User findeUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserById(UUID userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public void logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

    @Override
    public boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean updatePassword(String otp, String newPassword) {
        Otp otpRecord = otpRepository.findById(otp).orElse(null);
        if (otpRecord == null || !"Verified".equals(otpRecord.getStatus())) {
            return false;
        }
        User user = userRepository.findByEmail(otpRecord.getEmail());
        if (user == null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRecord.setStatus("Used");
        otpRepository.save(otpRecord);

        return true;
    }
}

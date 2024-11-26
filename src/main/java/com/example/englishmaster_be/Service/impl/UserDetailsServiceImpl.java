package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.CustomUserDetails;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username);
        if (user == null || !user.isEnabled()) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(user);
    }
}

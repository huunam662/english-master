package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.model.CustomUserDetails;
import com.example.englishmaster_be.model.User;
import com.example.englishmaster_be.repository.UserRepository;

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

package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Configuration.jwt.JwtUtils;
import com.example.englishmaster_be.DTO.UserLoginDTO;
import com.example.englishmaster_be.DTO.UserRegisterDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;

    @Override
    public ResponseModel registerUser(UserRegisterDTO userRegisterDTO) {
        ResponseModel responseModel = new ResponseModel();
        User user= new User();

        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setName(userRegisterDTO.getName());
        user.setRole(roleRepository.findByRoleId(UUID.fromString("876e0c22-7c28-47b1-abe8-589260227b07")));

        boolean existingUser = userRepository.existsByEmail(user.getEmail());
        if (existingUser){
            responseModel.setMessage("This email already exists!");
            responseModel.setStatus("fail");
            return responseModel;
        }


        userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);


        confirmationTokenRepository.save(confirmationToken);


        sendConfirmationEmail(user.getEmail(), confirmationToken.getUserConfirmTokenId().toString());

        responseModel.setMessage("Sent a confirmation mail!");
        responseModel.setStatus("success");

        return responseModel;
    }

    @Override
    public ResponseModel confirmRegister(String confirmToken) {
        System.out.println(confirmToken) ;

        ResponseModel responseModel = new ResponseModel();

        ConfirmationToken confirmationToken = confirmationTokenRepository.findByUserConfirmTokenId(UUID.fromString(confirmToken));
        if (confirmationToken == null){
            responseModel.setMessage("Invalid verification code!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        if (confirmationToken.getUser().isEnabled()){
            responseModel.setMessage("Account has been verified!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        if (confirmationToken.getConfirmExpiry().isBefore(LocalDateTime.now())){
            responseModel.setMessage("Verification code has expired!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        User user = confirmationToken.getUser();

        user.setEnabled(true);
        userRepository.save(user);

        responseModel.setMessage("Account has been successfully verified!");
        responseModel.setStatus("success");
        return responseModel;
    }

    @Override
    public ResponseModel loginUser(UserLoginDTO userLoginDTO) {
        ResponseModel responseModel = new ResponseModel();
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            AuthResponse authResponse = new AuthResponse(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities(), jwt);

            responseModel.setMessage("login successful");
            responseModel.setStatus("success");
            responseModel.setResponseData(authResponse);
            return responseModel;
        }catch (AuthenticationException e){
            responseModel.setMessage("login fail");
            responseModel.setStatus("fail");
            return responseModel;
        }
    }

    @Override
    public ResponseModel forgetPassword(String email) {
        ResponseModel responseModel = new ResponseModel();

        boolean existingUser = userRepository.existsByEmail(email);
        if (existingUser){
            responseModel.setMessage("This email don't exists!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        User user = userRepository.findByEmail(email);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenRepository.save(confirmationToken);

        return responseModel;
    }

    private void sendConfirmationEmail(String email, String confirmationToken){
        String confirmationLink = "http://localhost:8080/api/register/confirm?token=" + confirmationToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Xác nhận tài khoản");
        message.setText("Xin chào,\n\nVui lòng nhấp vào liên kết sau để xác nhận tài khoản:\n\n" + confirmationLink);
        mailSender.send(message);
    }

    private void sendForgetPassEmail(String email, String confirmationToken){
        String confirmationLink = "http://localhost:8080/api/forgetPass/confirm?token=" + confirmationToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Quên tài khoản");
        message.setText("Xin chào,\n\nVui lòng nhấp vào liên kết sau để thay đổi tài khoản:\n\n" + confirmationLink);
        mailSender.send(message);
    }
}

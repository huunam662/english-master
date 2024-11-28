package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Constant.RoleConstant;
import com.example.englishmaster_be.DTO.ConfirmationToken.CreateConfirmationTokenDTO;
import com.example.englishmaster_be.DTO.UserRegisterDTO;
import com.example.englishmaster_be.Exception.Response.ResourceNotFoundException;
import com.example.englishmaster_be.Exception.Response.ResponseNotFoundException;
import com.example.englishmaster_be.Mapper.UserMapper;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.ConfirmationTokenResponse;
import com.example.englishmaster_be.Model.Response.UserResponse;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    private final ConfirmationTokenServiceImpl confirmationTokenService;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${masterE.linkFE}")
    private String linkFE;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public UserResponse createUser(UserRegisterDTO userRegisterDTO) {
        boolean existingUser = userRepository.existsByEmail(userRegisterDTO.getEmail());

        if (existingUser) {
            User userExist = userRepository.findByEmail(userRegisterDTO.getEmail());
            if (!userExist.isEnabled()) {
                userRepository.delete(userExist);
            }
        }

        User userNew = userMapper.toEntity(userRegisterDTO);
        userNew.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        userNew.setRole(roleRepository.findByRoleName(RoleConstant.USER));

        try {
            userNew = userRepository.save(userNew);
        } catch (DataIntegrityViolationException exception) {
            throw new ResourceNotFoundException("User already exists");
        }

        ConfirmationTokenResponse confirmationTokenResponse = confirmationTokenService.createConfirmationToken(
                CreateConfirmationTokenDTO.builder().user(userNew).build()
        );

        try {
            sendConfirmationEmail(userNew.getEmail(), confirmationTokenResponse.getCode());
        } catch (IOException | MessagingException e) {
            throw new ResponseNotFoundException("Failed to send confirmation email", e);
        }

        return userMapper.toDto(userNew);
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
    public User findUserByEmail(String email) {
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
    private void sendConfirmationEmail(String email, String confirmationToken) throws IOException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String confirmationLink = linkFE + "register/confirm?token=" + confirmationToken;


        String templateContent = readTemplateContent("email_templates.html");
        templateContent = templateContent.replace("{{linkConfirm}}", confirmationLink);
        templateContent = templateContent.replace("{{btnConfirm}}", "Confirm");
        templateContent = templateContent.replace("{{nameLink}}", "Confirm Register");


        helper.setTo(email);
        helper.setSubject("Xác nhận tài khoản");
        helper.setText(templateContent, true);
        mailSender.send(message);
    }

    private String readTemplateContent(String templateFileName) throws IOException {
        Resource templateResource = resourceLoader.getResource("classpath:templates/" + templateFileName);
        byte[] templateBytes = FileCopyUtils.copyToByteArray(templateResource.getInputStream());
        return new String(templateBytes, StandardCharsets.UTF_8);
    }

}

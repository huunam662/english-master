package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.ConfirmationToken.SaveConfirmationTokenDTO;
import com.example.englishmaster_be.Mapper.ConfirmationTokenMapper;
import com.example.englishmaster_be.Model.ConfirmationToken;
import com.example.englishmaster_be.Model.Response.ConfirmationTokenResponse;
import com.example.englishmaster_be.Repository.ConfirmationTokenRepository;
import com.example.englishmaster_be.Service.IConfirmationTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfirmationTokenServiceImpl implements IConfirmationTokenService {

    ConfirmationTokenRepository confirmationTokenRepository;


    @Transactional
    @Override
    public ConfirmationTokenResponse createConfirmationToken(SaveConfirmationTokenDTO createConfirmationTokenDTO) {

        ConfirmationToken confirmationToken = ConfirmationTokenMapper.INSTANCE.toConfirmationToken(createConfirmationTokenDTO);

        confirmationToken.setType("ACTIVE");
        confirmationToken.setUser(createConfirmationTokenDTO.getUser());
        confirmationToken.setCode(UUID.randomUUID().toString());
        confirmationToken.setCreateAt(LocalDateTime.now());
        confirmationToken = confirmationTokenRepository.save(confirmationToken);

        return ConfirmationTokenMapper.INSTANCE.toConfirmationTokenResponse(confirmationToken);
    }

}

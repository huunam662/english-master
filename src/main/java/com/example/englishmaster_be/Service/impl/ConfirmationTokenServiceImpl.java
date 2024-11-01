package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.ConfirmationToken.CreateConfirmationTokenDTO;
import com.example.englishmaster_be.Mapper.ConfirmationTokenMapper;
import com.example.englishmaster_be.Model.ConfirmationToken;
import com.example.englishmaster_be.Model.Response.ConfirmationTokenResponse;
import com.example.englishmaster_be.Repository.ConfirmationTokenRepository;
import com.example.englishmaster_be.Service.IConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements IConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final ConfirmationTokenMapper confirmationTokenMapper;

    @Override
    public ConfirmationTokenResponse createConfirmationToken(CreateConfirmationTokenDTO createConfirmationTokenDTO) {

        ConfirmationToken confirmationToken = confirmationTokenMapper.toConfirmationToken(createConfirmationTokenDTO);

        confirmationToken.setType("ACTIVE");
        confirmationToken.setUser(createConfirmationTokenDTO.getUser());
        confirmationToken.setCode(UUID.randomUUID().toString());
        confirmationToken.setCreateAt(LocalDateTime.now());
        confirmationToken = confirmationTokenRepository.save(confirmationToken);

        return confirmationTokenMapper.toConfirmationTokenResponse(confirmationToken);
    }

}

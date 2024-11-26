package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.dto.confirmationToken.CreateConfirmationTokenDTO;
import com.example.englishmaster_be.mapper.ConfirmationTokenMapper;
import com.example.englishmaster_be.model.ConfirmationToken;
import com.example.englishmaster_be.model.response.ConfirmationTokenResponse;
import com.example.englishmaster_be.repository.ConfirmationTokenRepository;
import com.example.englishmaster_be.service.IConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements IConfirmationTokenService {
    @Autowired
    private ConfirmationTokenMapper confirmationTokenMapper;

    private final ConfirmationTokenRepository confirmationTokenRepository;

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

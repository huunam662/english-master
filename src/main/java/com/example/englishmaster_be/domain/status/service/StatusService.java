package com.example.englishmaster_be.domain.status.service;

import com.example.englishmaster_be.mapper.StatusMapper;
import com.example.englishmaster_be.domain.status.dto.request.StatusRequest;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.model.status.QStatusEntity;
import com.example.englishmaster_be.model.status.StatusEntity;
import com.example.englishmaster_be.model.status.StatusRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StatusService implements IStatusService {

    JPAQueryFactory queryFactory;

    StatusRepository statusRepository;


    @Transactional
    @Override
    public StatusEntity saveStatus(StatusRequest statusRequest) {

        StatusEntity status;

        if(statusRequest.getStatusId() != null)
            status = getStatusById(statusRequest.getStatusId());
        else
            status = StatusEntity.builder().build();

        StatusMapper.INSTANCE.flowToStatusEntity(statusRequest, status);

        return statusRepository.save(status);
    }

    @Override
    public boolean isExistedByStatusNameOfType(String statusName) {

        return statusRepository.isExistedByStatusNameOfType(statusName);
    }


    @Override
    public void deleteStatus(UUID statusId) {

        StatusEntity status = getStatusById(statusId);

        statusRepository.delete(status);
    }

    @Override
    public StatusEntity getStatusById(UUID statusId) {

        return statusRepository.findById(statusId).orElseThrow(
                () -> new ErrorHolder(Error.STATUS_NOT_FOUND)
        );
    }

    @Override
    public StatusEntity getStatusByName(String statusName) {

        return statusRepository.findByStatusName(statusName).orElseThrow(
                () -> new ErrorHolder(Error.STATUS_NOT_FOUND)
        );
    }

}

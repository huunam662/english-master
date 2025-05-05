package com.example.englishmaster_be.domain.status.service;

import com.example.englishmaster_be.converter.StatusConverter;
import com.example.englishmaster_be.domain.status.dto.request.StatusRequest;
import com.example.englishmaster_be.advice.exception.template.CustomException;
import com.example.englishmaster_be.common.constant.error.ErrorEnum;
import com.example.englishmaster_be.advice.exception.template.BadRequestException;
import com.example.englishmaster_be.domain.type.service.ITypeService;
import com.example.englishmaster_be.model.status.QStatusEntity;
import com.example.englishmaster_be.model.status.StatusEntity;
import com.example.englishmaster_be.model.type.TypeEntity;
import com.example.englishmaster_be.model.status.StatusRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StatusService implements IStatusService {

    JPAQueryFactory queryFactory;

    StatusRepository statusRepository;

    ITypeService typeService;


    @Transactional
    @Override
    public StatusEntity saveStatus(StatusRequest statusRequest) {

        StatusEntity status;

        TypeEntity type = typeService.getTypeById(statusRequest.getTypeId());

        if(statusRequest.getStatusId() != null)
            status = getStatusById(statusRequest.getStatusId());
        else
            status = StatusEntity.builder().build();

        if(isExistedByStatusNameOfType(statusRequest.getStatusName(), type))
            throw new BadRequestException(String.format("Status name of type %s already exist", type.getTypeName()));

        StatusConverter.INSTANCE.flowToStatusEntity(statusRequest, status);

        status.setType(type);

        return statusRepository.save(status);
    }

    @Override
    public boolean isExistedByStatusNameOfType(String statusName, TypeEntity type) {

        return statusRepository.isExistedByStatusNameOfType(statusName, type);
    }

    @Override
    public List<StatusEntity> getAllStatusByType(UUID typeId) {

        QStatusEntity status = QStatusEntity.statusEntity;

        JPAQuery<StatusEntity> query = queryFactory.selectFrom(status)
                .where(status.type.typeId.eq(typeId));

        return query.fetch();
    }

    @Override
    public void deleteStatus(UUID statusId) {

        StatusEntity status = getStatusById(statusId);

        statusRepository.delete(status);
    }

    @Override
    public StatusEntity getStatusById(UUID statusId) {

        return statusRepository.findById(statusId).orElseThrow(
                () -> new CustomException(ErrorEnum.STATUS_NOT_FOUND)
        );
    }

    @Override
    public StatusEntity getStatusByName(String statusName) {

        return statusRepository.findByStatusName(statusName).orElseThrow(
                () -> new CustomException(ErrorEnum.STATUS_NOT_FOUND)
        );
    }

}

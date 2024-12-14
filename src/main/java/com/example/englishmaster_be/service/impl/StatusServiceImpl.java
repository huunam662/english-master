package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.common.constaint.StatusEnum;
import com.example.englishmaster_be.mapper.StatusMapper;
import com.example.englishmaster_be.model.request.Status.StatusRequest;
import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.common.constaint.error.ErrorEnum;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.entity.QStatusEntity;
import com.example.englishmaster_be.service.ITypeService;
import com.example.englishmaster_be.entity.StatusEntity;
import com.example.englishmaster_be.entity.TypeEntity;
import com.example.englishmaster_be.repository.StatusRepository;
import com.example.englishmaster_be.service.IStatusService;
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
public class StatusServiceImpl implements IStatusService {

    JPAQueryFactory queryFactory;

    StatusRepository statusRepository;

    ITypeService typeService;


    @Transactional
    @Override
    public StatusEntity saveStatus(StatusRequest statusRequest) {

        StatusEntity status;

        TypeEntity type = typeService.getTypeById(statusRequest.getTypeId());

        String messageException = "StatusEntity is already exist";

        if(statusRequest.getStatusId() != null) {

            status = getStatusById(statusRequest.getStatusId());

            if(isExistedByStatusNameWithDiff(status, statusRequest.getStatusName()))
                throw new BadRequestException(messageException);
        }
        else{

            if(isExistedByStatusName(statusRequest.getStatusName()))
                throw new BadRequestException(messageException);

            status = StatusEntity.builder().build();
        }

        StatusMapper.INSTANCE.flowToStatusEntity(statusRequest, status);
        status.setType(type);

        return statusRepository.save(status);
    }

    @Override
    public boolean isExistedByStatusNameWithDiff(StatusEntity status, StatusEnum statusName) {

        return statusRepository.isExistedByStatusNameWithDiff(status, statusName);
    }

    @Override
    public boolean isExistedByStatusName(StatusEnum statusName) {

        return statusRepository.findByStatusName(statusName).isPresent();
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
    public StatusEntity getStatusByName(StatusEnum statusName) {

        return statusRepository.findByStatusName(statusName).orElseThrow(
                () -> new CustomException(ErrorEnum.STATUS_NOT_FOUND)
        );
    }

}

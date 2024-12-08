package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Constant.StatusConstant;
import com.example.englishmaster_be.DTO.Status.SaveStatusDTO;
import com.example.englishmaster_be.DTO.Status.UpdateStatusDTO;
import com.example.englishmaster_be.Exception.CustomException;
import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Model.QStatus;
import com.example.englishmaster_be.Model.Response.StatusResponse;
import com.example.englishmaster_be.Model.Status;
import com.example.englishmaster_be.Model.Type;
import com.example.englishmaster_be.Repository.StatusRepository;
import com.example.englishmaster_be.Repository.TypeRepository;
import com.example.englishmaster_be.Service.IStatusService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StatusServiceImpl implements IStatusService {

    JPAQueryFactory queryFactory;

    StatusRepository statusRepository;

    TypeRepository typeRepository;

    @Override
    public StatusResponse saveStatus(SaveStatusDTO statusDTO) {

        Status status;

        Type type = typeRepository.findById(statusDTO.getTypeId()).orElseThrow(
                () -> new BadRequestException("Type not found")
        );

        if(statusDTO instanceof UpdateStatusDTO updateStatusDTO){

            status = getStatusById(updateStatusDTO.getUpdateStatusId());

            status.setFlag(statusDTO.isFlag());
            status.setStatusName(statusDTO.getStatusName());
            status.setType(type);
        }
        else{
            status = statusRepository.findByStatusName(statusDTO.getStatusName()).orElse(null);

            if(status != null) throw new BadRequestException("Status is already exist");

            status = Status.builder()
                    .statusName(statusDTO.getStatusName())
                    .flag(statusDTO.isFlag())
                    .type(type)
                    .build();

        }

        status = statusRepository.save(status);

        return StatusResponse.builder()
                .statusId(status.getStatusId())
                .statusName(status.getStatusName())
                .flag(status.isFlag())
                .typeId(status.getType().getTypeId())
                .build();
    }

    @Override
    public List<StatusResponse> getAllStatusByType(UUID typeId) {
        QStatus status = QStatus.status;

        JPAQuery<Status> query = queryFactory.selectFrom(status)
                .where(status.type.typeId.eq(typeId));

        List<Status> statusList = query.fetch();

        return statusList.stream()
                .map(StatusResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteStatus(UUID statusId) {
        statusRepository.deleteById(statusId);
    }

    @Override
    public Status getStatusById(UUID statusId) {

        return statusRepository.findById(statusId).orElseThrow(
                () -> new CustomException(Error.STATUS_NOT_FOUND)
        );
    }

    @Override
    public Status getStatusByName(String statusName) {

        return statusRepository.findByStatusName(statusName).orElseThrow(
                () -> new CustomException(Error.STATUS_NOT_FOUND)
        );
    }

}

package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.status.CreateStatusDTO;
import com.example.englishmaster_be.DTO.status.UpdateStatusDTO;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StatusServiceImpl implements IStatusService {
    StatusRepository statusRepository;
    JPAQueryFactory queryFactory;
    TypeRepository typeRepository;

    @Override
    public StatusResponse createStatus(CreateStatusDTO statusDTO) {
        Status status = statusRepository.save(Status.builder()
                .statusName(statusDTO.getStatusName())
                .flag(statusDTO.isFlag())
                .type(typeRepository.findById(statusDTO.getTypeId()).orElse(null))
                .build());
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
    public StatusResponse getStatusById(UUID statusId) {
        Status status = statusRepository.findById(statusId).orElse(null);
        if (status == null) {
            return null;
        }
        return new StatusResponse(status);
    }

    @Override
    public StatusResponse updateStatus(UpdateStatusDTO statusDTO) {
        Status status = statusRepository.findById(statusDTO.getId()).orElse(null);
        Type type = typeRepository.findById(statusDTO.getTypeId()).orElse(null);
        if (status == null) {
            return null;
        }
        if (type == null) {
            return null;
        }
        status.setStatusName(statusDTO.getStatusName());
        status.setFlag(statusDTO.isFlag());
        status.setType(type);

        statusRepository.save(status);
        return new StatusResponse(status);
    }
}

package com.example.englishmaster_be.domain.exam.pack.type.dto.view;

import com.example.englishmaster_be.domain.exam.pack.type.model.PackTypeEntity;
import lombok.Data;

public interface IPackTypePageView {

    PackTypeEntity getPackType();
    Long getCountPacks();

    @Data
    class PackTypePageView implements IPackTypePageView{
        private PackTypeEntity packType;
        private Long countPacks;
    }
}

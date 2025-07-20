package com.example.englishmaster_be.domain.exam.pack.pack.dto.view;

import com.example.englishmaster_be.domain.exam.pack.pack.model.PackEntity;
import lombok.Data;

public interface IPackPageView {

    PackEntity getPack();
    Long getCountTopics();

    @Data
    class PackPageView implements IPackPageView{
        private PackEntity pack;
        private Long countTopics;
    }

}

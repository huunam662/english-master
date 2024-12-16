package com.example.englishmaster_be.mapper;


import com.example.englishmaster_be.domain.post.dto.response.PostResponse;
import com.example.englishmaster_be.model.post.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);


    @Mapping(target = "userPostId", source = "userPost.userId")
    @Mapping(target = "username", source = "userPost.name")
    @Mapping(target = "userAvatar", source = "userPost.avatar")
    @Mapping(target = "numberComment", expression = "java(postEntity.getComments() != null ? postEntity.getComments().size() : 0)")
    PostResponse toPostResponse(PostEntity postEntity);

    List<PostResponse> toPostResponseList(List<PostEntity> postEntityList);



}

package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.entity.CommentEntity;
import com.example.englishmaster_be.Model.Response.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "contentComment", source = "content")
    @Mapping(target = "topicId", source = "topic.topicId")
    @Mapping(target = "postId", source = "post.postId")
    @Mapping(target = "tagAuthorParent", source = "commentParent.userComment.name")
    @Mapping(target = "authorComment", source = "userComment")
    @Mapping(target = "hasCommentParent", expression = "java(comment != null && comment.getCommentParent() != null)")
    @Mapping(target = "hasCommentChildren", expression = "java(comment != null && comment.getCommentChildren() != null && !comment.getCommentChildren().isEmpty())")
    CommentResponse toCommentResponse(CommentEntity comment);

    List<CommentResponse> toCommentResponseList(List<CommentEntity> commentList);

}

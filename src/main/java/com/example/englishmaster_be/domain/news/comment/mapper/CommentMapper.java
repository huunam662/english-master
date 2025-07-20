package com.example.englishmaster_be.domain.news.comment.mapper;

import com.example.englishmaster_be.domain.news.comment.dto.res.CommentChildRes;
import com.example.englishmaster_be.domain.news.comment.dto.res.CommentNewsRes;
import com.example.englishmaster_be.domain.news.comment.model.CommentEntity;
import com.example.englishmaster_be.domain.news.comment.dto.res.CommentRes;
import com.example.englishmaster_be.domain.user.user.mapper.UserMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(imports = {UserMapper.class}, builder = @Builder(disableBuilder = true))
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "contentComment", source = "content")
    @Mapping(target = "topicId", source = "topic.topicId")
    @Mapping(target = "newsId", source = "news.newsId")
    @Mapping(target = "tagAuthorParent", source = "commentParent.userComment.name")
    @Mapping(target = "authorComment", source = "userComment")
    @Mapping(target = "hasCommentParent", expression = "java(comment != null && comment.getCommentParent() != null)")
    @Mapping(target = "hasCommentChildren", expression = "java(comment != null && comment.getCommentChildren() != null && !comment.getCommentChildren().isEmpty())")
    CommentRes toCommentResponse(CommentEntity comment);

    List<CommentRes> toCommentResponseList(Collection<CommentEntity> commentList);

    @Mapping(target = "timeOfComment", source = "createAt")
    @Mapping(target = "commentContent", source = "content")
    @Mapping(target = "authorComment", expression = "java(UserMapper.INSTANCE.toAuthorCommentResponse(comment.getUserComment()))")
    CommentNewsRes toCommentNewsResponse(CommentEntity comment);

    List<CommentNewsRes> toCommentNewsResponseList(Collection<CommentEntity> commentList);

    @Mapping(target = "timeOfComment", source = "createAt")
    @Mapping(target = "commentContent", source = "content")
    @Mapping(target = "commentToOwnerTag", source = "toOwnerComment.name")
    @Mapping(target = "authorComment", expression = "java(UserMapper.INSTANCE.toAuthorCommentResponse(comment.getUserComment()))")
    CommentChildRes toCommentChildResponse(CommentEntity comment);

    List<CommentChildRes> toCommentChildResponseList(Collection<CommentEntity> commentList);
}

package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.comment.dto.response.CommentChildResponse;
import com.example.englishmaster_be.domain.comment.dto.response.CommentNewsResponse;
import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.domain.comment.dto.response.CommentResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "contentComment", source = "content")
    @Mapping(target = "topicId", source = "topic.topicId")
    @Mapping(target = "newsId", source = "news.newsId")
    @Mapping(target = "tagAuthorParent", source = "commentParent.userComment.name")
    @Mapping(target = "authorComment", source = "userComment")
    @Mapping(target = "hasCommentParent", expression = "java(comment != null && comment.getCommentParent() != null)")
    @Mapping(target = "hasCommentChildren", expression = "java(comment != null && comment.getCommentChildren() != null && !comment.getCommentChildren().isEmpty())")
    CommentResponse toCommentResponse(CommentEntity comment);

    List<CommentResponse> toCommentResponseList(Collection<CommentEntity> commentList);

    @Mapping(target = "timeOfComment", source = "createAt")
    @Mapping(target = "commentContent", source = "content")
    @Mapping(target = "numberOfVotes", expression = "java(comment.getUsersVotes().size())")
    @Mapping(target = "numberOfCommentsChild", expression = "java(comment.getCommentChildren().size())")
    @Mapping(target = "authorComment", expression = "java(UserMapper.INSTANCE.toAuthorCommentResponse(comment.getUserComment()))")
    CommentNewsResponse toCommentNewsResponse(CommentEntity comment);

    List<CommentNewsResponse> toCommentNewsResponseList(Collection<CommentEntity> commentList);

    @Mapping(target = "timeOfComment", source = "createAt")
    @Mapping(target = "commentContent", source = "content")
    @Mapping(target = "numberOfVotes", expression = "java(comment.getUsersVotes().size())")
    @Mapping(target = "commentToOwnerTag", source = "toOwnerComment.name")
    @Mapping(target = "authorComment", expression = "java(UserMapper.INSTANCE.toAuthorCommentResponse(comment.getUserComment()))")
    CommentChildResponse toCommentChildResponse(CommentEntity comment);

    List<CommentChildResponse> toCommentChildResponseList(Collection<CommentEntity> commentList);
}

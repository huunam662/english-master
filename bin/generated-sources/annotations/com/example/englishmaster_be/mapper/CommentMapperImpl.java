package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.comment.dto.response.CommentAuthorResponse;
import com.example.englishmaster_be.domain.comment.dto.response.CommentResponse;
import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.post.PostEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:23+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class CommentMapperImpl implements CommentMapper {

    @Override
    public CommentResponse toCommentResponse(CommentEntity comment) {
        if ( comment == null ) {
            return null;
        }

        CommentResponse.CommentResponseBuilder commentResponse = CommentResponse.builder();

        commentResponse.contentComment( comment.getContent() );
        commentResponse.topicId( commentTopicTopicId( comment ) );
        commentResponse.postId( commentPostPostId( comment ) );
        commentResponse.tagAuthorParent( commentCommentParentUserCommentName( comment ) );
        commentResponse.authorComment( userEntityToCommentAuthorResponse( comment.getUserComment() ) );
        commentResponse.commentId( comment.getCommentId() );
        commentResponse.updateAt( comment.getUpdateAt() );

        commentResponse.hasCommentParent( comment != null && comment.getCommentParent() != null );
        commentResponse.hasCommentChildren( comment != null && comment.getCommentChildren() != null && !comment.getCommentChildren().isEmpty() );

        return commentResponse.build();
    }

    @Override
    public List<CommentResponse> toCommentResponseList(List<CommentEntity> commentList) {
        if ( commentList == null ) {
            return null;
        }

        List<CommentResponse> list = new ArrayList<CommentResponse>( commentList.size() );
        for ( CommentEntity commentEntity : commentList ) {
            list.add( toCommentResponse( commentEntity ) );
        }

        return list;
    }

    private UUID commentTopicTopicId(CommentEntity commentEntity) {
        TopicEntity topic = commentEntity.getTopic();
        if ( topic == null ) {
            return null;
        }
        return topic.getTopicId();
    }

    private UUID commentPostPostId(CommentEntity commentEntity) {
        PostEntity post = commentEntity.getPost();
        if ( post == null ) {
            return null;
        }
        return post.getPostId();
    }

    private String commentCommentParentUserCommentName(CommentEntity commentEntity) {
        CommentEntity commentParent = commentEntity.getCommentParent();
        if ( commentParent == null ) {
            return null;
        }
        UserEntity userComment = commentParent.getUserComment();
        if ( userComment == null ) {
            return null;
        }
        return userComment.getName();
    }

    protected CommentAuthorResponse userEntityToCommentAuthorResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        CommentAuthorResponse.CommentAuthorResponseBuilder commentAuthorResponse = CommentAuthorResponse.builder();

        commentAuthorResponse.userId( userEntity.getUserId() );
        commentAuthorResponse.name( userEntity.getName() );
        commentAuthorResponse.email( userEntity.getEmail() );
        commentAuthorResponse.avatar( userEntity.getAvatar() );

        return commentAuthorResponse.build();
    }
}

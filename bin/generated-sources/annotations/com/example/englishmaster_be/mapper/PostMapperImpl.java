package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.post.dto.response.PostResponse;
import com.example.englishmaster_be.model.post.PostEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class PostMapperImpl implements PostMapper {

    @Override
    public PostResponse toPostResponse(PostEntity postEntity) {
        if ( postEntity == null ) {
            return null;
        }

        PostResponse.PostResponseBuilder postResponse = PostResponse.builder();

        postResponse.userPostId( postEntityUserPostUserId( postEntity ) );
        postResponse.username( postEntityUserPostName( postEntity ) );
        postResponse.userAvatar( postEntityUserPostAvatar( postEntity ) );
        postResponse.postId( postEntity.getPostId() );
        postResponse.content( postEntity.getContent() );
        postResponse.createAt( postEntity.getCreateAt() );
        postResponse.updateAt( postEntity.getUpdateAt() );

        postResponse.numberComment( postEntity.getComments() != null ? postEntity.getComments().size() : 0 );

        return postResponse.build();
    }

    @Override
    public List<PostResponse> toPostResponseList(List<PostEntity> postEntityList) {
        if ( postEntityList == null ) {
            return null;
        }

        List<PostResponse> list = new ArrayList<PostResponse>( postEntityList.size() );
        for ( PostEntity postEntity : postEntityList ) {
            list.add( toPostResponse( postEntity ) );
        }

        return list;
    }

    private UUID postEntityUserPostUserId(PostEntity postEntity) {
        UserEntity userPost = postEntity.getUserPost();
        if ( userPost == null ) {
            return null;
        }
        return userPost.getUserId();
    }

    private String postEntityUserPostName(PostEntity postEntity) {
        UserEntity userPost = postEntity.getUserPost();
        if ( userPost == null ) {
            return null;
        }
        return userPost.getName();
    }

    private String postEntityUserPostAvatar(PostEntity postEntity) {
        UserEntity userPost = postEntity.getUserPost();
        if ( userPost == null ) {
            return null;
        }
        return userPost.getAvatar();
    }
}

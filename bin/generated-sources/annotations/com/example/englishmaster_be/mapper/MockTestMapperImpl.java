package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.mock_test.dto.request.MockTestRequest;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestPartResponse;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
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
public class MockTestMapperImpl implements MockTestMapper {

    @Override
    public MockTestResponse toMockTestResponse(MockTestEntity mockTestEntity) {
        if ( mockTestEntity == null ) {
            return null;
        }

        MockTestResponse.MockTestResponseBuilder mockTestResponse = MockTestResponse.builder();

        mockTestResponse.topicId( mockTestEntityTopicTopicId( mockTestEntity ) );
        mockTestResponse.mockTestId( mockTestEntity.getMockTestId() );
        mockTestResponse.correctAnswers( mockTestEntity.getCorrectAnswers() );
        mockTestResponse.score( mockTestEntity.getScore() );
        mockTestResponse.time( mockTestEntity.getTime() );
        mockTestResponse.createAt( mockTestEntity.getCreateAt() );
        mockTestResponse.updateAt( mockTestEntity.getUpdateAt() );
        mockTestResponse.user( userEntityToUserBasicResponse( mockTestEntity.getUser() ) );
        mockTestResponse.userCreate( userEntityToUserBasicResponse( mockTestEntity.getUserCreate() ) );
        mockTestResponse.userUpdate( userEntityToUserBasicResponse( mockTestEntity.getUserUpdate() ) );

        return mockTestResponse.build();
    }

    @Override
    public List<MockTestResponse> toMockTestResponseList(List<MockTestEntity> mockTestEntityList) {
        if ( mockTestEntityList == null ) {
            return null;
        }

        List<MockTestResponse> list = new ArrayList<MockTestResponse>( mockTestEntityList.size() );
        for ( MockTestEntity mockTestEntity : mockTestEntityList ) {
            list.add( toMockTestResponse( mockTestEntity ) );
        }

        return list;
    }

    @Override
    public MockTestEntity toMockTestEntity(MockTestRequest mockTestRequest) {
        if ( mockTestRequest == null ) {
            return null;
        }

        MockTestEntity.MockTestEntityBuilder mockTestEntity = MockTestEntity.builder();

        mockTestEntity.score( mockTestRequest.getScore() );
        mockTestEntity.time( mockTestRequest.getTime() );

        return mockTestEntity.build();
    }

    @Override
    public MockTestPartResponse toPartMockTestResponse(MockTestEntity mockTestEntity) {
        if ( mockTestEntity == null ) {
            return null;
        }

        MockTestPartResponse.MockTestPartResponseBuilder mockTestPartResponse = MockTestPartResponse.builder();

        mockTestPartResponse.topicId( mockTestEntityTopicTopicId( mockTestEntity ) );
        mockTestPartResponse.topicName( mockTestEntityTopicTopicName( mockTestEntity ) );
        mockTestPartResponse.topicTime( mockTestEntityTopicWorkTime( mockTestEntity ) );

        return mockTestPartResponse.build();
    }

    private UUID mockTestEntityTopicTopicId(MockTestEntity mockTestEntity) {
        TopicEntity topic = mockTestEntity.getTopic();
        if ( topic == null ) {
            return null;
        }
        return topic.getTopicId();
    }

    protected UserBasicResponse userEntityToUserBasicResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserBasicResponse.UserBasicResponseBuilder userBasicResponse = UserBasicResponse.builder();

        userBasicResponse.userId( userEntity.getUserId() );
        userBasicResponse.name( userEntity.getName() );

        return userBasicResponse.build();
    }

    private String mockTestEntityTopicTopicName(MockTestEntity mockTestEntity) {
        TopicEntity topic = mockTestEntity.getTopic();
        if ( topic == null ) {
            return null;
        }
        return topic.getTopicName();
    }

    private String mockTestEntityTopicWorkTime(MockTestEntity mockTestEntity) {
        TopicEntity topic = mockTestEntity.getTopic();
        if ( topic == null ) {
            return null;
        }
        return topic.getWorkTime();
    }
}

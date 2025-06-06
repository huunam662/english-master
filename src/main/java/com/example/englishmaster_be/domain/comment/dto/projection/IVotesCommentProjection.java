package com.example.englishmaster_be.domain.comment.dto.projection;

import java.util.UUID;

public interface IVotesCommentProjection {

    UUID getCommentId();

    int getCountVotes();

}

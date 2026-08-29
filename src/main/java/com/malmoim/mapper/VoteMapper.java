package com.malmoim.mapper;


public interface VoteMapper {

    void castVote(long questionNo, Long participantNo);
}

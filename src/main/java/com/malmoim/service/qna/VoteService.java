package com.malmoim.service.qna;

public interface VoteService {
    void castVote(long roomNo, long questionNo, Long participantNo);
}

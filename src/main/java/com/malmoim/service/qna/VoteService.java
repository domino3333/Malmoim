package com.malmoim.service.qna;

public interface VoteService {
    void castVote(long questionNo, Long participantNo);
}

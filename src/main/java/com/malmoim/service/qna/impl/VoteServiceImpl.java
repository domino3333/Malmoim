package com.malmoim.service.qna.impl;

import com.malmoim.mapper.VoteMapper;
import com.malmoim.service.qna.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteMapper voteMapper;


    @Override
    public void castVote(long questionNo, Long participantNo) {
        voteMapper.castVote(questionNo,participantNo);
    }
}

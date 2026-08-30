package com.malmoim.service.qna.impl;

import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.dto.qna.QnaRoomInfoResponse;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.VoteMapper;
import com.malmoim.service.qna.QnaRoomService;
import com.malmoim.service.qna.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteMapper voteMapper;
    private final QnaRoomMapper qnaRoomMapper;


    @Override
    public void castVote(long roomNo,long questionNo, Long participantNo) {

        QnaRoom qnaRoom = qnaRoomMapper.selectOneQnaRoomByRoomNo(roomNo);

        QnaPhase status = qnaRoom.getStatus();

        if(status != QnaPhase.VOTING_OPEN){
            throw new RuntimeException("현재 방의 status가 voting_open이 아닙니다");
        }

        


        voteMapper.castVote(questionNo,participantNo);
    }
}

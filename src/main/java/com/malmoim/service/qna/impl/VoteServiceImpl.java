package com.malmoim.service.qna.impl;

import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.mapper.VoteMapper;
import com.malmoim.service.qna.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteMapper voteMapper;
    private final QnaRoomMapper qnaRoomMapper;
    private final QuestionMapper questionMapper;


    @Override
    @Transactional
    public void castVote(long roomNo,long questionNo, Long participantNo) {

        // questionNo를 받았을 때 그 질문이 실제로 넘겨받은 roomNo에 있는지 검증
        Integer isExistsQuestion = questionMapper.isExistsQuestionInTheRoom(roomNo,questionNo);

        if(isExistsQuestion==0){
            throw new RuntimeException("%d번 질문에 해당하는 방이 %d번방에 존재하지 않습니다".formatted(questionNo,roomNo));
        }


        QnaRoom qnaRoom = qnaRoomMapper.selectQnaRoomByRoomNo(roomNo);

        QnaPhase status = qnaRoom.getStatus();

        if(status != QnaPhase.VOTING_OPEN){
            throw new RuntimeException("현재 방의 status가 voting_open이 아닙니다");
        }

        voteMapper.castVote(questionNo,participantNo);
        questionMapper.incrementVoteCount(questionNo);


    }
}

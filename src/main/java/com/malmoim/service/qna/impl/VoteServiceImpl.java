package com.malmoim.service.qna.impl;

import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.mapper.VoteMapper;
import com.malmoim.service.qna.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteMapper voteMapper;
    private final QnaRoomMapper qnaRoomMapper;
    private final QuestionMapper questionMapper;


    @Override
    @Transactional
    public void castVote(long roomNo, long questionNo, Long participantNo) {

        // questionNo를 받았을 때 그 질문이 실제로 넘겨받은 roomNo에 있는지 검증
        Integer questionExists = questionMapper.existsByRoomNoAndQuestionNo(roomNo, questionNo);

        if (questionExists == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "%d번 질문이 %d번 방에 존재하지 않습니다".formatted(questionNo, roomNo));
        }


        QnaRoom qnaRoom = qnaRoomMapper.selectQnaRoomByRoomNo(roomNo);
        if (qnaRoom == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "방을 찾을 수 없습니다.");
        }

        QnaPhase status = qnaRoom.getStatus();

        LocalDateTime now = LocalDateTime.now();
        if (status != QnaPhase.VOTING_OPEN || !now.isBefore(qnaRoom.getVotingEndedAt())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "현재, 투표가 가능한 상태가 아닙니다.");
        }

        try {
            voteMapper.castVote(questionNo, participantNo);
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 투표한 질문입니다.", e);
        }
        questionMapper.incrementVoteCount(questionNo);


    }
}

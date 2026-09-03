package com.malmoim.service.qna.impl;

import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.domain.Question;
import com.malmoim.dto.qna.presence.ParticipantPresenceResponse;
import com.malmoim.dto.qna.question.QuestionCreatedMessage;
import com.malmoim.dto.qna.question.QuestionResponse;
import com.malmoim.dto.qna.vote.VoteResultResponse;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.service.qna.QnaRoomService;
import com.malmoim.service.qna.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {


    private final QuestionMapper questionMapper;
    private final QnaRoomMapper qnaRoomMapper;
    private final QnaRoomService qnaRoomService;

    @Override
    @Transactional
    public QuestionCreatedMessage createQuestion(long roomNo, long participantNo, String question, String nickname) {

        QnaRoom qnaRoom = qnaRoomMapper.selectQnaRoomByRoomNo(roomNo);

        if (qnaRoom == null || qnaRoom.getStatus() != QnaPhase.QUESTION_OPEN) {
            throw new RuntimeException("질문 등록이 가능한 상태가 아닙니다");
        }


        //매퍼에서 디폴트 값으로 넣으면 useGenerated로 못돌려받음, 여기서 직접 넣기
        Question savedQuestion = Question.builder()
                .roomNo(roomNo)
                .participantNo(participantNo)
                .status("WAITING")
                .createdAt(LocalDateTime.now())
                .voteCount(0)
                .content(question)
                .build();

        questionMapper.insertQuestion(savedQuestion);

        return QuestionCreatedMessage.builder()
                .questionNo(savedQuestion.getNo())
                .participantNo(participantNo)
                .content(question)
                .nickname(nickname)
                .createdAt(savedQuestion.getCreatedAt())
                .status(savedQuestion.getStatus())
                .roomNo(roomNo)
                .voteCount(savedQuestion.getVoteCount())
                .build();


    }

    @Override
    public List<QuestionResponse> getQuestionList(Long roomNo) {

        return questionMapper.getQuestionList(roomNo);
    }

    @Override
    public List<VoteResultResponse> getSortedQuestionList(String hostEmail, long roomNo) {

        return questionMapper.getSortedQuestionListByRoomNo(roomNo);
    }

    @Override
    public List<VoteResultResponse> revealResults(String hostEmail, long roomNo) {

        // host의 방 소유권 검사
        boolean ownsRoom = qnaRoomService.validateRoomOwnership(roomNo, hostEmail);

        if (!ownsRoom) {
            throw new RuntimeException("방에 대한 권한이 없습니다.");
        }

        //방의 현 상태가 투표 종료인지 검사
        QnaRoom qnaRoom = qnaRoomMapper.selectQnaRoomByRoomNo(roomNo);
        if(qnaRoom.getStatus()!=QnaPhase.VOTING_CLOSED){
            throw new RuntimeException("방의 현 status가 투표 종료 상태가 아닙니다.");
        }

        return getSortedQuestionList(hostEmail,roomNo);

    }


}

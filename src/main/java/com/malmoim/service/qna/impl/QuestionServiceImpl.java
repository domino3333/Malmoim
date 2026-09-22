package com.malmoim.service.qna.impl;

import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.domain.Question;
import com.malmoim.domain.QuestionStatus;
import com.malmoim.dto.qna.question.ToggleAnswerStatusResponse;
import com.malmoim.dto.qna.question.QuestionCreatedMessage;
import com.malmoim.dto.qna.question.QuestionResponse;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.service.qna.QuestionService;
import com.malmoim.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {


    private final QuestionMapper questionMapper;
    private final QnaRoomMapper qnaRoomMapper;
    private final RoomService roomService;

    @Override
    @Transactional
    public QuestionCreatedMessage createQuestion(long roomNo, long participantNo, String question, String nickname) {

        QnaRoom qnaRoom = qnaRoomMapper.selectQnaRoomByRoomNo(roomNo);

        LocalDateTime now = LocalDateTime.now();
        if (qnaRoom == null || qnaRoom.getStatus() != QnaPhase.QUESTION_OPEN
                || !now.isBefore(qnaRoom.getQuestionEndedAt())) {
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
    public List<QuestionResponse> getQuestionsByRoomNo(Long roomNo) {

        return questionMapper.selectQuestionsByRoomNo(roomNo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getHostQuestionList(long roomNo, String hostEmail) {
        roomService.validateRoomOwnership(roomNo, hostEmail);
        return getQuestionsByRoomNo(roomNo);
    }

    @Override
    public List<QuestionResponse> getRankedQuestionsByRoomNo(long roomNo) {

        return questionMapper.selectRankedQuestionsByRoomNo(roomNo);
    }

    public Integer updateQuestionStatus(long questionNo, QuestionStatus status) {

        return questionMapper.updateQuestionStatus(questionNo, status);
    }

    @Override
    @Transactional
    public ToggleAnswerStatusResponse toggleAnswerStatus(String hostEmail, Long roomNo, Long questionNo) {

        roomService.validateRoomOwnership(roomNo, hostEmail);

        Integer exist = questionMapper.existsByRoomNoAndQuestionNo(roomNo, questionNo);

        if (exist == null||exist == 0) {
            throw new AccessDeniedException("roomNo와 questionNo가 교차하는 row가 존재하지 않습니다.");
        }

        Question question = questionMapper.selectQuestionByQuestionNo(questionNo);
        if (question == null || question.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다.");
        }

        QuestionStatus nextStatus;
        if (QuestionStatus.WAITING.name().equals(question.getStatus())) {
            nextStatus = QuestionStatus.ANSWERED;
        } else if (QuestionStatus.ANSWERED.name().equals(question.getStatus())) {
            nextStatus = QuestionStatus.WAITING;
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "변경할 수 없는 질문 상태입니다.");
        }

        updateQuestionStatus(questionNo, nextStatus);

        return new ToggleAnswerStatusResponse(question.getNo(), nextStatus.name());


    }


}

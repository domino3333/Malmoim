package com.malmoim.service.qna.impl;

import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.domain.Question;
import com.malmoim.dto.qna.QuestionCreatedMessage;
import com.malmoim.dto.qna.QuestionListResponse;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.service.qna.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {


    private final QuestionMapper questionMapper;
    private final QnaRoomMapper qnaRoomMapper;

    @Override
    @Transactional
    public QuestionCreatedMessage registerQuestion(long roomNo, long participantNo, String question, String nickname) {

        QnaRoom qnaRoom = qnaRoomMapper.selectOneQnaRoomByRoomNo(roomNo);

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

        questionMapper.registerQuestion(savedQuestion);

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
    public List<QuestionListResponse> getQuestionList(Long roomNo) {

        return questionMapper.getQuestionList(roomNo);
    }
}

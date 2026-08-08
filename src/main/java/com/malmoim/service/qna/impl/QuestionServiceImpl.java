package com.malmoim.service.qna.impl;

import com.malmoim.domain.Question;
import com.malmoim.dto.qna.QuestionCreatedMessage;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.service.qna.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {


    private final QuestionMapper questionMapper;

    @Override
    public QuestionCreatedMessage registerQuestion(long roomNo, long participantNo, String question, String nickname) {

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
                .no(savedQuestion.getNo())
                .participantNo(participantNo)
                .content(question)
                .nickname(nickname)
                .createdAt(savedQuestion.getCreatedAt())
                .status(savedQuestion.getStatus())
                .roomNo(roomNo)
                .voteCount(savedQuestion.getVoteCount())
                .build();


    }
}

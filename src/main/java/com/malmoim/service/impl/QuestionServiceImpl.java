package com.malmoim.service.impl;

import com.malmoim.domain.Question;
import com.malmoim.dto.room.qna.QuestionCreatedMessage;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {


    private final QuestionMapper questionMapper;

    @Override
    public QuestionCreatedMessage registerQuestion(long roomNo, long participantNo, String question, String nickname) {

        Question savedQuestion = Question.builder()
                .roomNo(roomNo)
                .participantNo(participantNo)
                .content(question)
                .build();

        questionMapper.registerQuestion(savedQuestion);

        return QuestionCreatedMessage.builder()
                .no(savedQuestion.getNo())
                .content(question)
                .nickname(nickname)
                .createdAt(savedQuestion.getCreatedAt())
                .status(savedQuestion.getStatus())
                .roomNo(roomNo)
                .voteCount(savedQuestion.getVoteCount())
                .build();


    }
}

package com.malmoim.mapper;


import com.malmoim.domain.Question;
import com.malmoim.domain.QuestionStatus;
import com.malmoim.dto.qna.question.QuestionResponse;

import java.util.List;

public interface QuestionMapper {

    void insertQuestion(Question question);

    List<QuestionResponse> selectQuestionsByRoomNo(Long roomNo);

    void incrementVoteCount(long questionNo);

    List<QuestionResponse> selectRankedQuestionsByRoomNo(long roomNo);

    Integer existsByRoomNoAndQuestionNo(long roomNo, long questionNo);

    Integer updateQuestionStatus(Long questionNo, QuestionStatus status);

    Question selectQuestionByQuestionNo(Long questionNo);
}

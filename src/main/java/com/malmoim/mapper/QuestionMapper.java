package com.malmoim.mapper;


import com.malmoim.domain.Question;
import com.malmoim.dto.qna.question.QuestionResponse;
import com.malmoim.dto.qna.vote.VoteResultResponse;

import java.util.List;

public interface QuestionMapper {

    void insertQuestion(Question question);

    List<QuestionResponse> getQuestionList(Long roomNo);

    void incrementVoteCount(long questionNo);

    List<VoteResultResponse> getSortedQuestionListByRoomNo(long roomNo);

    Integer isExistsQuestionInTheRoom(long roomNo, long questionNo);
}

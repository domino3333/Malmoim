package com.malmoim.mapper;


import com.malmoim.domain.Question;
import com.malmoim.domain.Room;
import com.malmoim.dto.qna.question.QuestionResponse;
import com.malmoim.dto.qna.vote.VoteResultResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionMapper {

    void insertQuestion(Question question);

    List<QuestionResponse> getQuestionList(Long roomNo);

    void incrementVoteCount(long questionNo);

    List<VoteResultResponse> getSortedQuestionListByRoomNo(long roomNo);
}

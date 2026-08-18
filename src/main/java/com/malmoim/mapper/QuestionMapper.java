package com.malmoim.mapper;


import com.malmoim.domain.Question;
import com.malmoim.domain.Room;
import com.malmoim.dto.qna.QuestionListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionMapper {

    void registerQuestion(Question question);

    List<QuestionListResponse> getQuestionList(Long roomNo);
}

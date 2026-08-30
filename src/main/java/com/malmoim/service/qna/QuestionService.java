package com.malmoim.service.qna;

import com.malmoim.dto.qna.QuestionCreatedMessage;
import com.malmoim.dto.qna.QuestionListResponse;

import java.util.List;

public interface QuestionService {
    QuestionCreatedMessage registerQuestion(long roomNo, long participantNo, String question, String nickname);

    List<QuestionListResponse> getQuestionList(Long roomNo);
}

package com.malmoim.service.qna;

import com.malmoim.dto.qna.question.QuestionCreatedMessage;
import com.malmoim.dto.qna.question.QuestionResponse;

import java.util.List;

public interface QuestionService {
    QuestionCreatedMessage registerQuestion(long roomNo, long participantNo, String question, String nickname);

    List<QuestionResponse> getQuestionList(Long roomNo);
}

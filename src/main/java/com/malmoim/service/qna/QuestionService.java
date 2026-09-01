package com.malmoim.service.qna;

import com.malmoim.dto.qna.question.QuestionCreatedMessage;
import com.malmoim.dto.qna.question.QuestionResponse;
import com.malmoim.dto.qna.vote.VoteResultResponse;

import java.util.List;

public interface QuestionService {
    QuestionCreatedMessage createQuestion(long roomNo, long participantNo, String question, String nickname);

    List<QuestionResponse> getQuestionList(Long roomNo);

    VoteResultResponse getSortedQuestionList(String hostEmail, long roomNo);
}

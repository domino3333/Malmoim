package com.malmoim.service.qna;

import com.malmoim.dto.qna.phase.AnsweringResultResponse;
import com.malmoim.dto.qna.question.QuestionCreatedMessage;
import com.malmoim.dto.qna.question.QuestionResponse;
import com.malmoim.dto.qna.vote.VoteResultResponse;

import java.util.List;

public interface QuestionService {
    QuestionCreatedMessage createQuestion(long roomNo, long participantNo, String question, String nickname);

    List<QuestionResponse> getQuestionList(Long roomNo);

    // 호스트 소유권 확인 후 질문 목록 조회
    List<QuestionResponse> getHostQuestionList(long roomNo, String hostEmail);

    List<VoteResultResponse> getSortedQuestionList(long roomNo);

    AnsweringResultResponse revealResults(String hostEmail, long roomNo);
}

package com.malmoim.service.qna;

import com.malmoim.dto.qna.question.ToggleAnswerStatusResponse;
import com.malmoim.dto.qna.question.QuestionCreatedMessage;
import com.malmoim.dto.qna.question.QuestionResponse;

import java.util.List;

public interface QuestionService {
    QuestionCreatedMessage createQuestion(long roomNo, long participantNo, String question, String nickname);

    List<QuestionResponse> getQuestionsByRoomNo(Long roomNo);

    // 호스트 소유권 확인 후 질문 목록 조회
    List<QuestionResponse> getHostQuestionList(long roomNo, String hostEmail);

    List<QuestionResponse> getRankedQuestionsByRoomNo(long roomNo);

    ToggleAnswerStatusResponse toggleQuestionStatus(String hostEmail, Long roomNo, Long questionNo);
}

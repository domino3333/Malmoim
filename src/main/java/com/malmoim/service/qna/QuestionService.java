package com.malmoim.service.qna;

import com.malmoim.dto.qna.QuestionCreatedMessage;

public interface QuestionService {
    QuestionCreatedMessage registerQuestion(long roomNo, long participantNo, String question, String nickname);
}

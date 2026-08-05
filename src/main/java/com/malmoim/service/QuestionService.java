package com.malmoim.service;

import com.malmoim.dto.room.qna.QuestionCreatedMessage;

public interface QuestionService {
    QuestionCreatedMessage registerQuestion(long roomNo, long participantNo, String question, String nickname);
}

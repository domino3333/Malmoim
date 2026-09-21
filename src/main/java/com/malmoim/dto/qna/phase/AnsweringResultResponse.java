package com.malmoim.dto.qna.phase;

import com.malmoim.dto.qna.question.QuestionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnsweringResultResponse {
    private QnaPhaseResponse qnaPhaseResponse;
    private List<QuestionResponse> questions;
}

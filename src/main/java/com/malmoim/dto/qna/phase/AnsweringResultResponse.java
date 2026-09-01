package com.malmoim.dto.qna.phase;

import com.malmoim.dto.qna.vote.VoteResultResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnsweringResultResponse {
    private QnaPhaseResponse qnaPhaseResponse;
    private List<VoteResultResponse> questions;
}

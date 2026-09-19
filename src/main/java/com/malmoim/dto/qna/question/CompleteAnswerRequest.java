package com.malmoim.dto.qna.question;


import com.malmoim.domain.QuestionStatus;
import lombok.Data;

@Data
public class CompleteAnswerRequest {

    private Long roomNo;
    private Long questionNo;
    private QuestionStatus status;
}

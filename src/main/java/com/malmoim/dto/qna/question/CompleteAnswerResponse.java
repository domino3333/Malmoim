package com.malmoim.dto.qna.question;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompleteAnswerResponse {
    private Long questionNo;
    private String status;
}

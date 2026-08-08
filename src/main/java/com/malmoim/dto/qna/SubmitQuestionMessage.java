package com.malmoim.dto.qna;


import lombok.Data;

@Data
public class SubmitQuestionMessage {
    private Long roomNo;
    private String question;

}

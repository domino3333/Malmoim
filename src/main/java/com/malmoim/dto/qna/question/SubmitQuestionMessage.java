package com.malmoim.dto.qna.question;


import lombok.Data;

@Data
public class SubmitQuestionMessage {
    private Long roomNo;
    private String question;

}

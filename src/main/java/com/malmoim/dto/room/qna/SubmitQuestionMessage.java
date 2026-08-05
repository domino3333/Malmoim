package com.malmoim.dto.room.qna;


import lombok.Data;

@Data
public class SubmitQuestionMessage {
    private Long roomNo;
    private Long participantNo;

    private String question;

}

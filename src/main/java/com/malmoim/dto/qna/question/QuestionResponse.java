package com.malmoim.dto.qna.question;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuestionResponse {

    private Long questionNo;
    private Long participantNo;
    private String nickname;
    private String content; //질문내용
    private Long roomNo;
    private Integer voteCount;
    private LocalDateTime createdAt;
    private String status;

}

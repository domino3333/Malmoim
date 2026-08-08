package com.malmoim.dto.qna;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuestionCreatedMessage {

    private Long no; // question No
    private Long participantNo;
    private String content;
    private Long roomNo;
    private String nickname;
    private Integer voteCount;
    private LocalDateTime createdAt;
    private String status;

}

package com.malmoim.dto.qna.vote;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VoteResultResponse {

    private Long no; // question테이블의 pk
    private Long participantNo;
    private String content;
    private Long roomNo;
    private Integer voteCount;
    private LocalDateTime createdAt;
    private String status;

}

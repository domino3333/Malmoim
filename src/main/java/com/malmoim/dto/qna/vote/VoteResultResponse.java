package com.malmoim.dto.qna.vote;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VoteResultResponse {

    private Long questionNo; // question테이블의 pk
    private Long participantNo;
    private String content;
    private Long roomNo;
    private Integer voteCount;
    private LocalDateTime createdAt;
    private String status;
    private String nickname;

}

package com.malmoim.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Question {

    private Long no; //questionNo
    private Long participantNo;
    private Long roomNo;
    private Integer voteCount;
    private String content;
    private LocalDateTime createdAt;
    private String status;
}

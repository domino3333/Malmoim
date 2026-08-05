package com.malmoim.domain;

import java.time.LocalDateTime;

public class Question {

    private Long no; //questionNo
    private Long participantNo;
    private Long roomNo;
    private Integer voteCount;
    private String content;
    private LocalDateTime createdAt;
    private String status;
}

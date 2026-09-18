package com.malmoim.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    private Long no; // question 테이블 PK
    private Long participantNo;
    private Long roomNo;
    private Integer voteCount;
    private String content;
    private LocalDateTime createdAt;
    private String status;
}

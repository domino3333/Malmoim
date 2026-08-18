package com.malmoim.dto.qna;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuestionListResponse {

    private Long no; //question_no
    private Long participant_no;
    private String content; //질문내용
    private Long roomNo;
    private Integer voteCount;
    private LocalDateTime createdAt;
    private String status;

}

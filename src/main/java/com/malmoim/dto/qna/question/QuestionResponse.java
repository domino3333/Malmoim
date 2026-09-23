package com.malmoim.dto.qna.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {

    private Long questionNo;
    private Long participantNo;
    private String nickname;
    private String content; //질문내용
    private Long roomNo;
    private Integer voteCount;
    private LocalDateTime createdAt;
    private String status;
    private Integer voteRank;

}

package com.malmoim.domain;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QnaRoom {

    private Long roomNo;
    private LocalDateTime questionStartedAt;
    private LocalDateTime questionEndedAt;
    private LocalDateTime votingStartedAt;
    private LocalDateTime votingEndedAt;
    private QnaPhase status;

}

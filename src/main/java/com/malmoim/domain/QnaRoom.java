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
public class QnaRoom {

    private Long roomNo;
    private LocalDateTime questionStartedAt;
    private LocalDateTime questionEndedAt;
    private LocalDateTime votingStartedAt;
    private LocalDateTime votingEndedAt;
    private QnaPhase status;

}

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
    private LocalDateTime questionEndsAt;
    private LocalDateTime votingStartedAt;
    private LocalDateTime votingEndsAt;
    private QnaPhase status;

}

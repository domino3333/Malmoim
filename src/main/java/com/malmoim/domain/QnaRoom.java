package com.malmoim.domain;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QnaRoom {

    private Long roomNo;
    private LocalDateTime questionStartedAt;
    private LocalDateTime questionEndedAt;
    
}

package com.malmoim.domain;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Participant {

    private Long no;
    private String nickname;
    private LocalDateTime createdAt;
    private Long roomNo;

}

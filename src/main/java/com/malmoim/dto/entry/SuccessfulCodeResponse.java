package com.malmoim.dto.entry;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SuccessfulCodeResponse {

    private long roomNo;
    private long hostNo;
    private String title;
    private String code;
    private int capacity;
    private String password;
    private LocalDateTime createdAt;
    private String status;
    private String type;
    private String visibility;
    private LocalDateTime questionStartedAt;
    private LocalDateTime questionEndedAt;
}

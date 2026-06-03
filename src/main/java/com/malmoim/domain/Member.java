package com.malmoim.domain;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@Builder
public class Member {

    private Long no;
    private String email;
    private String password;
    private String name;
    private String role;
    private LocalDateTime createdAt;
}

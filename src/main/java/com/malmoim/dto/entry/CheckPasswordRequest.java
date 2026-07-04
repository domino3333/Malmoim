package com.malmoim.dto.entry;


import lombok.Data;

@Data
public class CheckPasswordRequest {

    private long roomNo;
    private String password;
}

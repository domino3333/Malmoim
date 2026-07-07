package com.malmoim.dto.entry;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsertNickResponse {
    private String message;
    private Long no;
}

package com.malmoim.dto.entry;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CheckPasswordRequest {

    @NotNull(message = "방 번호가 필요합니다")
    @Positive(message = "방 번호가 올바르지 않습니다")
    private Long roomNo;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}

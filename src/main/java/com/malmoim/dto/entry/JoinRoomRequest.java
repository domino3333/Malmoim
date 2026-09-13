package com.malmoim.dto.entry;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JoinRoomRequest {
    @NotNull(message = "방 번호가 필요합니다")
    @Positive(message = "방 번호가 올바르지 않습니다")
    private Long roomNo;

    @NotBlank(message = "닉네임을 입력해주세요")
    @Size(max = 20, message = "닉네임은 20자 이하여야 합니다")
    private String nickname;

    private String password;
}

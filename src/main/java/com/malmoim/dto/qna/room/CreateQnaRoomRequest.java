package com.malmoim.dto.qna.room;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateQnaRoomRequest {

    @NotBlank(message = "제목을 입력해주세요")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다")
    private String title;

    @NotNull(message = "정원을 입력해주세요")
    @Positive(message = "정원은 1명 이상이어야 합니다")
    private Integer capacity;

    private String password;

    @NotNull(message = "공개 여부를 선택해주세요")
    private Boolean isPrivate;

}

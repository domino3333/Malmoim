package com.malmoim.dto.qna.question;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ToggleAnswerStatusRequest {

    @NotNull(message = "방 번호가 필요합니다")
    @Positive(message = "방 번호가 올바르지 않습니다")
    private Long roomNo;

    @NotNull(message = "질문 번호가 필요합니다")
    @Positive(message = "질문 번호가 올바르지 않습니다")
    private Long questionNo;
}

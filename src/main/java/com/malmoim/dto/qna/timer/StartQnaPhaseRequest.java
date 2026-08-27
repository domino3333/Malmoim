package com.malmoim.dto.qna.timer;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StartQnaPhaseRequest {

    @NotNull(message = "시간을 반드시 입력")
    @Positive(message = "시간은 1초 이상")
    @Max(value = 3600, message = "시간은 최대 60분")
    private Long durationSeconds;
}

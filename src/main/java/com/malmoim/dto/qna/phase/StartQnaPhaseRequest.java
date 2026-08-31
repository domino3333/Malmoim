package com.malmoim.dto.qna.phase;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StartQnaPhaseRequest {

    //null, 0, 음수를 막기위함
    @NotNull(message = "시간을 반드시 입력") // null인지 검사
    @Positive(message = "시간은 1초 이상") //0보다 큰 값인지 검사
    @Max(value = 3600, message = "시간은 최대 60분")
    private Long durationSeconds;
}

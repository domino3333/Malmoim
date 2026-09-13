package com.malmoim.dto.qna.question;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubmitQuestionMessage {
    private Long roomNo;

    @NotBlank(message = "질문을 입력해주세요")
    @Size(max = 1000, message = "질문은 1000자 이하여야 합니다")
    private String question;

}

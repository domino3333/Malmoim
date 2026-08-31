package com.malmoim.dto.qna.phase;

import com.malmoim.domain.QnaPhase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomStatusRequest {
    private QnaPhase status;
}

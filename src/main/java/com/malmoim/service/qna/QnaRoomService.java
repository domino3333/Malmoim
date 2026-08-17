package com.malmoim.service.qna;

import com.malmoim.dto.qna.CreateQnaRoomRequest;
import com.malmoim.dto.qna.timer.StartTimerResponse;

public interface QnaRoomService {

    void createQnaRoom(CreateQnaRoomRequest dto, String hostEmail);

    // 질문 시간 설정 및 질문 접수 단계 시작
    StartTimerResponse startQuestionPhase(String hostEmail, long durationSeconds, long roomNo);

    void updateRoomStatus(String hostEmail, long roomNo, String status);

    boolean validateRoomOwnership(long roomNo,String hostEmail);
}

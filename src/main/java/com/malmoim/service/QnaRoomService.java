package com.malmoim.service;

import com.malmoim.domain.Room;
import com.malmoim.dto.room.MyRoomsResponseDto;
import com.malmoim.dto.room.qna.timer.StartTimerResponse;

public interface QnaRoomService {

    // 질문 시간 설정 및 질문 접수 단계 시작.
    StartTimerResponse startQuestionPhase(String hostEmail, long durationSeconds, long roomNo);

    void updateRoomStatus(String hostEmail,long roomNo,  String status);
}

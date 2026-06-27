package com.malmoim.service;

import com.malmoim.domain.Room;
import com.malmoim.dto.room.MyRoomsResponseDto;
import com.malmoim.dto.room.qna.CreateQnaRoomDto;
import com.malmoim.dto.room.qna.timer.StartTimerResponse;

public interface QnaRoomService {

    StartTimerResponse updateQuestionStartedAt(String hostEmail, long durationSeconds, long roomNo);

    void updateRoomStatus(String hostEmail,long roomNo,  String status);
}

package com.malmoim.service;

import com.malmoim.domain.Room;
import com.malmoim.dto.room.MyRoomsResponseDto;
import com.malmoim.dto.room.qna.CreateQnaRoomDto;

public interface QnaRoomService {

    void insertQuestionStartedAt(String hostEmail,long durationSeconds,long roomNo);
}

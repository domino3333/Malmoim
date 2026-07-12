package com.malmoim.mapper;


import com.malmoim.domain.QnaRoom;
import com.malmoim.dto.room.qna.timer.StartTimerResponse;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

public interface QnaRoomMapper {


    void insertQnaRoom(QnaRoom qnaRoom);

    void updateQuestionPeriod(@Param("roomNo") long roomNo, @Param("startedAt") LocalDateTime startedAt, @Param("endedAt") LocalDateTime endedAt);

    StartTimerResponse selectQuestionTimerByRoomNo(long roomNo);

    void updateRoomStatus(long roomNo, String status);
}

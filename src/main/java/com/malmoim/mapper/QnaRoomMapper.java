package com.malmoim.mapper;


import com.malmoim.domain.QnaRoom;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

public interface QnaRoomMapper {


    void createQnaRoom(QnaRoom qnaRoom);

    void updateQuestionStartedAt(@Param("roomNo") long roomNo, @Param("startedAt") LocalDateTime startedAt, @Param("endedAt") LocalDateTime endedAt);

    QnaRoom selectStEndedAtOfTheRoom(long roomNo);
}

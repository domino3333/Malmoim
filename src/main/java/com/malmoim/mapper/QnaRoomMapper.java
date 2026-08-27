package com.malmoim.mapper;


import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.dto.qna.QnaPhaseResponse;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

public interface QnaRoomMapper {


    // qna_room 테이블에 Q&A 방 정보 추가
    void insertQnaRoom(QnaRoom qnaRoom);

    // 질문 접수 시작 시간과 종료 시간 수정
    void updateQuestionPeriod(@Param("roomNo") long roomNo, @Param("startedAt") LocalDateTime startedAt, @Param("endedAt") LocalDateTime endedAt);

    // 방 번호 기준 질문 타이머 정보 조회
    QnaPhaseResponse selectQuestionTimerByRoomNo(long roomNo);

    Integer updateRoomStatus(@Param("roomNo") Long roomNo, @Param("status") QnaPhase status);

    QnaRoom selectOneQnaRoomByRoomNo(long roomNo);

    void updateVotingPeriod(long roomNo, LocalDateTime startedAt, LocalDateTime endedAt);
}

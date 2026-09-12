package com.malmoim.mapper;


import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.dto.qna.phase.QnaPhaseResponse;
import com.malmoim.dto.qna.room.QnaRoomInfoResponse;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QnaRoomMapper {


    // qna_room 테이블에 Q&A 방 정보 추가
    void insertQnaRoom(QnaRoom qnaRoom);

    // 방 번호 기준 Q&A 방 정보와 현재 페이즈 시간 조회
    QnaRoomInfoResponse selectQnaRoomInfoByRoomNo(@Param("roomNo") Long roomNo);

    // 질문 접수 시작 시간과 종료 시간 수정
    void updateQuestionPeriod(@Param("roomNo") long roomNo, @Param("startedAt") LocalDateTime startedAt, @Param("endedAt") LocalDateTime endedAt);

    // 방 번호 기준 질문 타이머 정보 조회
    QnaPhaseResponse selectQuestionPhaseByRoomNo(long roomNo);

    QnaPhaseResponse selectVotingPhaseByRoomNo(long roomNo);

    Integer updateQnaPhase(@Param("roomNo") Long roomNo, @Param("status") QnaPhase status);

    QnaRoom selectQnaRoomByRoomNo(long roomNo);

    void updateVotingPeriod(long roomNo, LocalDateTime startedAt, LocalDateTime endedAt);

    /**
    *  시간이 만료된 방을 db에서 검증해서 내려주기 위해 추가한 매퍼
    * */
    //질문 시간이 종료된 방들을 받아옴
    List<Long> selectExpiredQuestionPhaseRoomNos(@Param("now")LocalDateTime now);

    //투표 시간이 종료된 방들을 받아옴
    List<Long> selectExpiredVotingPhaseRoomNos(@Param("now")LocalDateTime now);

    //질문이 시간이 만료된 방의 상태를 question_closed로 바꾸는 매퍼
    int closeExpiredQuestionPhase(Long roomNo, LocalDateTime now);
}

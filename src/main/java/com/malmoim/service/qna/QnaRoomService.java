package com.malmoim.service.qna;

import com.malmoim.domain.QnaPhase;
import com.malmoim.dto.qna.phase.QnaPhaseResponse;
import com.malmoim.dto.qna.room.CreateQnaRoomRequest;
import com.malmoim.dto.qna.room.QnaRoomInfoResponse;

public interface QnaRoomService {

    void createQnaRoom(CreateQnaRoomRequest dto, String hostEmail);

    // 로그인한 호스트 소유의 Q&A 방 조회
    QnaRoomInfoResponse getOwnedRoomByNo(long roomNo, String hostEmail);

    // 방 번호 기준 단일 Q&A 방 조회
    QnaRoomInfoResponse getRoomByNo(Long roomNo);

    // 질문 시간 설정 및 질문 접수 단계 시작
    QnaPhaseResponse startQuestionPhase(String hostEmail, long durationSeconds, long roomNo);

    //투표 페이즈 시작
    QnaPhaseResponse startVotingPhase(String hostEmail, long durationSeconds, long roomNo);

    //넘겨받은 status로 방의 상태를 업데이트
    QnaPhaseResponse updateQnaPhase(String hostEmail, long roomNo, QnaPhase status);

    //호스트의 room인지 검증
    boolean validateRoomOwnership(long roomNo,String hostEmail);
}

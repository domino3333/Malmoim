package com.malmoim.service.qna;

import com.malmoim.domain.QnaPhase;
import com.malmoim.dto.qna.CreateQnaRoomRequest;
import com.malmoim.dto.qna.QnaPhaseResponse;

public interface QnaRoomService {

    void createQnaRoom(CreateQnaRoomRequest dto, String hostEmail);

    // 질문 시간 설정 및 질문 접수 단계 시작
    QnaPhaseResponse startQuestionPhase(String hostEmail, long durationSeconds, long roomNo);

    //투표 페이즈 시작
    QnaPhaseResponse startVotingPhase(String hostEmail, long durationSeconds, long roomNo);

    QnaPhaseResponse updateRoomStatus(String hostEmail, long roomNo, QnaPhase status);

    //호스트의 room인지 검증
    boolean validateRoomOwnership(long roomNo,String hostEmail);
}

package com.malmoim.service.qna.impl;

import com.malmoim.domain.Member;
import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.domain.Room;
import com.malmoim.dto.qna.phase.QnaPhaseResponse;
import com.malmoim.dto.qna.room.CreateQnaRoomRequest;
import com.malmoim.dto.qna.room.QnaRoomInfoResponse;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.RoomMapper;
import com.malmoim.service.qna.QnaRoomService;
import com.malmoim.service.room.RoomService;
import com.malmoim.util.RoomCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QnaRoomServiceImpl implements QnaRoomService {

    private final QnaRoomMapper qnaRoomMapper;
    private final MemberMapper memberMapper;
    private final RoomMapper roomMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoomService roomService;

    @Override
    @Transactional
    public void createQnaRoom(CreateQnaRoomRequest dto, String hostEmail) {
        String code = RoomCodeGenerator.generate();
        log.info("random code:{}", code);

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // code가 이미 존재한다면 다시 발급
        while (roomMapper.countRoomsByCode(code) >= 1) {
            code = RoomCodeGenerator.generate();
        }

        // 로그인한 사용자(host)의 정보 가져오기
        Member host = memberMapper.getMemberByEmail(hostEmail);
        if (host == null) {
            throw new UsernameNotFoundException("host가 없습니다.");
        }

        // room 생성
        Room room = Room.builder()
                .hostNo(host.getNo())
                .title(dto.getTitle())
                .capacity(dto.getCapacity())
                .password(encodedPassword)
                .code(code)
                .type("QNA")
                .visibility(dto.getIsPrivate() ? "PRIVATE" : "PUBLIC")
                .build();

        roomMapper.insertRoom(room);

        // room에 종속받는 1:1 구조의 qna_room 생성
        qnaRoomMapper.insertQnaRoom(QnaRoom.builder()
                .roomNo(room.getNo())
                .status(QnaPhase.READY)
                .build());
    }

    @Override
    // 로그인한 호스트 소유의 Q&A 방 조회
    public QnaRoomInfoResponse getOwnedRoomByNo(long roomNo, String hostEmail) {
        roomService.validateRoomOwnership(roomNo, hostEmail);

        return roomMapper.selectRoomByNo(roomNo);
    }

    @Override
    // 방 번호 기준 단일 Q&A 방 조회
    public QnaRoomInfoResponse getRoomByNo(Long roomNo) {
        return roomMapper.selectRoomByNo(roomNo);
    }

    @Override
    @Transactional
    // 질문 시간 저장 및 방 상태를 QUESTION_OPEN으로 변경
    public QnaPhaseResponse startQuestionPhase(String hostEmail, long durationSeconds, long roomNo) {
        roomService.validateRoomOwnership(roomNo, hostEmail);

        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime endedAt = startedAt.plusSeconds(durationSeconds);
        log.info("startQuestionPhase 질문 시작 시간 :{}", startedAt);

        qnaRoomMapper.updateQuestionPeriod(roomNo, startedAt, endedAt);
        qnaRoomMapper.updateQnaPhase(roomNo, QnaPhase.QUESTION_OPEN);

        return qnaRoomMapper.selectQuestionPhaseByRoomNo(roomNo);
    }

    @Override
    @Transactional
    // 투표 시작
    public QnaPhaseResponse startVotingPhase(String hostEmail, long durationSeconds, long roomNo) {
        roomService.validateRoomOwnership(roomNo, hostEmail);

        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime endedAt = startedAt.plusSeconds(durationSeconds);
        log.info("startVotingPhase 투표 시작 시간 :{}", startedAt);

        QnaRoom qnaRoom = qnaRoomMapper.selectQnaRoomByRoomNo(roomNo);
        if(qnaRoom == null || qnaRoom.getStatus()!=QnaPhase.QUESTION_CLOSED){
            throw new RuntimeException("현재 질문 종료 페이즈가 아니므로 투표를 시작할 수 없습니다.");
        }

        qnaRoomMapper.updateVotingPeriod(roomNo, startedAt, endedAt);
        qnaRoomMapper.updateQnaPhase(roomNo, QnaPhase.VOTING_OPEN);

        return qnaRoomMapper.selectVotingPhaseByRoomNo(roomNo);
    }

    @Override
    @Transactional
    public QnaPhaseResponse updateQnaPhase(String hostEmail, long roomNo, QnaPhase status) {
        roomService.validateRoomOwnership(roomNo, hostEmail);

        qnaRoomMapper.updateQnaPhase(roomNo, status);
        QnaRoom room = qnaRoomMapper.selectQnaRoomByRoomNo(roomNo);

        return new QnaPhaseResponse(roomNo,room.getStatus(),null,null);


    }

}

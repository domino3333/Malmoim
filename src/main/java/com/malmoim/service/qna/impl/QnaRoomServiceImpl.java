package com.malmoim.service.qna.impl;

import com.malmoim.domain.Member;
import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.domain.Room;
import com.malmoim.dto.qna.CreateQnaRoomRequest;
import com.malmoim.dto.qna.QnaPhaseResponse;
import com.malmoim.dto.qna.QnaRoomInfoResponse;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.RoomMapper;
import com.malmoim.service.qna.QnaRoomService;
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
    @Transactional
    // 질문 시간 저장 및 방 상태를 QUESTION_OPEN으로 변경
    public QnaPhaseResponse startQuestionPhase(String hostEmail, long durationSeconds, long roomNo) {
        Member host = memberMapper.getMemberByEmail(hostEmail);

        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime endedAt = startedAt.plusSeconds(durationSeconds);
        log.info("startQuestionPhase 질문 시작 시간 :{}", startedAt);

        Integer isHostsRoom = roomMapper.isHostsRoom(roomNo,host.getNo());
        if (isHostsRoom < 1) {
            throw new RuntimeException("호스트의 방을 찾을 수 없습니다.");
        }

        qnaRoomMapper.updateQuestionPeriod(roomNo, startedAt, endedAt);
        qnaRoomMapper.updateRoomStatus(roomNo, QnaPhase.QUESTION_OPEN);

        return qnaRoomMapper.selectQuestionTimerByRoomNo(roomNo);
    }

    @Override
    @Transactional
    // 투표 시작
    public QnaPhaseResponse startVotingPhase(String hostEmail, long durationSeconds, long roomNo) {
        Member host = memberMapper.getMemberByEmail(hostEmail);

        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime endedAt = startedAt.plusSeconds(durationSeconds);
        log.info("startVotingPhase 투표 시작 시간 :{}", startedAt);

        QnaRoom qnaRoom = qnaRoomMapper.selectOneQnaRoomByRoomNo(roomNo);
        if(qnaRoom.getStatus()!=QnaPhase.QUESTION_CLOSED){
            throw new RuntimeException("현재 질문 종료 페이즈가 아니므로 투표를 시작할 수 없습니다.");
        }

        Integer isHostsRoom = roomMapper.isHostsRoom(roomNo,host.getNo());
        if (isHostsRoom < 1) {
            throw new RuntimeException("호스트의 방을 찾을 수 없습니다.");
        }

        qnaRoomMapper.updateVotingPeriod(roomNo, startedAt, endedAt);
        qnaRoomMapper.updateRoomStatus(roomNo, QnaPhase.VOTING_OPEN);

        return qnaRoomMapper.selectVotingTimerByRoomNo(roomNo);
    }

    @Override
    @Transactional
    public QnaPhaseResponse updateRoomStatus(String hostEmail, long roomNo, QnaPhase status) {
        Member host = memberMapper.getMemberByEmail(hostEmail);

        if (roomMapper.isHostsRoom(roomNo,host.getNo()) != 1) {
            throw new RuntimeException("%s 에 해당하는 방이 없습니다.".formatted(hostEmail));
        }

        qnaRoomMapper.updateRoomStatus(roomNo, status);
        QnaRoom room = qnaRoomMapper.selectOneQnaRoomByRoomNo(roomNo);

        return new QnaPhaseResponse(roomNo,room.getStatus(),null,null);


    }

    @Override
    public boolean validateRoomOwnership(long roomNo, String hostEmail) {
        Member host = memberMapper.getMemberByEmail(hostEmail);

        QnaRoomInfoResponse room = roomMapper.selectRoomByNoAndHostNo(roomNo, host.getNo());

        if(room==null){
            return false;
        }

        return true;

    }
}

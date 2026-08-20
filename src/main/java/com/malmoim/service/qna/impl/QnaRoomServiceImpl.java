package com.malmoim.service.qna.impl;

import com.malmoim.domain.Member;
import com.malmoim.domain.QnaRoom;
import com.malmoim.domain.Room;
import com.malmoim.dto.qna.CreateQnaRoomRequest;
import com.malmoim.dto.qna.timer.StartTimerResponse;
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
                .build());
    }

    @Override
    @Transactional
    // 질문 시간 저장 및 방 상태를 QUESTION_OPEN으로 변경
    public StartTimerResponse startQuestionPhase(String hostEmail, long durationSeconds, long roomNo) {
        Member host = memberMapper.getMemberByEmail(hostEmail);

        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime endedAt = startedAt.plusSeconds(durationSeconds);
        log.info("qna 서비스단 질문 시작 시간 :{}", startedAt);

        Integer hostRoomCount = roomMapper.countMyRooms(host.getNo());
        if (hostRoomCount < 1) {
            throw new RuntimeException("호스트의 방을 찾을 수 없습니다.");
        }

        qnaRoomMapper.updateQuestionPeriod(roomNo, startedAt, endedAt);
        roomMapper.updateRoomStatus(host.getNo(), roomNo, "QUESTION_OPEN");

        return qnaRoomMapper.selectQuestionTimerByRoomNo(roomNo);
    }

    @Override
    public Integer updateRoomStatus(String hostEmail, long roomNo, String status) {
        Member host = memberMapper.getMemberByEmail(hostEmail);



        //todo 이거 바꿔야함
        if (roomMapper.countMyRooms(host.getNo()) < 1) {
            throw new RuntimeException("%s 에 해당하는 방이 없습니다.".formatted(hostEmail));
        }

        return roomMapper.updateRoomStatus(host.getNo(), roomNo, status);


    }

    @Override
    public boolean validateRoomOwnership(long roomNo, String hostEmail) {
        Member host = memberMapper.getMemberByEmail(hostEmail);

        Room room = roomMapper.selectRoomByNoAndHostNo(roomNo, host.getNo());

        if(room==null){
            return false;
        }

        return true;

    }
}

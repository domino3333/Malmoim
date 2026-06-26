package com.malmoim.service.impl;

import com.malmoim.domain.Member;
import com.malmoim.domain.QnaRoom;
import com.malmoim.domain.Room;
import com.malmoim.dto.room.MyRoomsResponseDto;
import com.malmoim.dto.room.qna.CreateQnaRoomDto;
import com.malmoim.dto.room.qna.timer.StartTimerResponse;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.RoomMapper;
import com.malmoim.service.QnaRoomService;
import com.malmoim.service.RoomService;
import com.malmoim.util.RoomCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class QnaRoomServiceImpl implements QnaRoomService {


    private final QnaRoomMapper qnaRoomMapper;
    private final MemberMapper memberMapper;
    private final RoomMapper roomMapper;


    @Override
    @Transactional
    public StartTimerResponse updateQuestionStartedAt(String hostEmail, long durationSeconds, long roomNo) {

        Member host = memberMapper.getMemberByEmail(hostEmail);

        //시작시간과 종료시간
        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime endedAt = startedAt.plusSeconds(durationSeconds);

        //hostEmail과 roomNo 로 일단 방이 있는지 확인
        Integer hostsRoomCount = roomMapper.countMyRooms(host.getNo());
        if (hostsRoomCount < 1) {
            throw new RuntimeException("호스트의 방을 찾을 수 없습니다.");
        }

        // 종료/시작 시간을 db에 업데이트
        qnaRoomMapper.updateQuestionStartedAt(roomNo,startedAt,endedAt);

        //동시에 그 방의 종료,시작 시간 가져오기
        QnaRoom qnaRoom = qnaRoomMapper.selectStEndedAtOfTheRoom(roomNo);


        return StartTimerResponse.builder()
                .roomNo(qnaRoom.getRoomNo())
                .questionStartedAt(qnaRoom.getQuestionStartedAt())
                .questionEndedAt(qnaRoom.getQuestionEndedAt()).build();



    }
}

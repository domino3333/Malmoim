package com.malmoim.service.entry.impl;

import com.malmoim.domain.Participant;
import com.malmoim.domain.Room;
import com.malmoim.dto.entry.*;
import com.malmoim.dto.qna.QnaRoomInfoResponse;
import com.malmoim.mapper.ParticipantMapper;
import com.malmoim.mapper.RoomMapper;
import com.malmoim.security.jwt.JwtTokenProvider;
import com.malmoim.service.entry.EntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntryServiceImpl implements EntryService {

    private final RoomMapper roomMapper;
    private final ParticipantMapper participantMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    // 입장 코드에 해당하는 참가자용 방 정보 응답 생성
    public CheckCodeResponse getRoomEntryInfo(String code) {
        if (roomMapper.countRoomsByCode(code) < 1) {
            throw new RuntimeException("코드에 해당하는 방이 존재하지 않습니다.");
        }

        Room room = roomMapper.selectRoomByCode(code);

        return CheckCodeResponse.builder()
                .roomNo(room.getNo())
                .title(room.getTitle())
                .code(room.getCode())
                .capacity(room.getCapacity())
                .hasPassword(room.getPassword() != null)
                .build();
    }

    @Override
    // 입력한 비밀번호와 저장된 방 비밀번호 비교
    public CheckPasswordResponse verifyRoomPassword(CheckPasswordRequest dto) {
        QnaRoomInfoResponse room = roomMapper.selectRoomByNo(dto.getRoomNo());

        if (room == null) {
            throw new RuntimeException("방을 찾을 수 없습니다.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), room.getPassword())) {
            log.info("verifyRoomPassword service 방 비밀번호 불일치");
            throw new RuntimeException("방의 비밀번호가 일치하지 않습니다.");
        }

        return new CheckPasswordResponse("비밀번호 일치");
    }

    @Override
    @Transactional
    // 참가자 저장 및 발급된 참가자 번호 반환
    public JoinRoomResponse joinRoom(JoinRoomRequest dto) {
        Participant participant = Participant.builder()
                .roomNo(dto.getRoomNo())
                .nickname(dto.getNickname())
                .build();

        participantMapper.insertParticipant(participant);
        Long participantNo = participant.getNo();
        String token = jwtTokenProvider.createParticipantToken(participantNo, dto.getRoomNo(), dto.getNickname());

        return new JoinRoomResponse(participantNo, "참여자 insert 완료", token);
    }
}

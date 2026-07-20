package com.malmoim.service.impl;

import com.malmoim.domain.Participant;
import com.malmoim.domain.Room;
import com.malmoim.dto.entry.*;
import com.malmoim.mapper.EntryMapper;
import com.malmoim.service.EntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntryServiceImpl implements EntryService {


    private final EntryMapper entryMapper;
    private final BCryptPasswordEncoder passwordEncoder;



    @Override
    // 입장 코드에 해당하는 참가자용 방 정보 응답 생성.
    public CheckCodeResponse getRoomEntryInfo(String code) {

        if(entryMapper.countRoomsByCode(code) < 1 ){
            throw new RuntimeException("코드에 해당하는 방이 존재하지 않습니다.");
        }

        //todo 나중엔 방마다 타입이 다를 텐데, 이때는 어떻게 해야 할지 생각해야 함
        //todo 이 코드는 체크코드 성공후에 닉네임 입력하게하고나서 구현할코드인데 일단 나중에 옮기기
//        if(room.getType().equals("QnA")){
//            //qna방일경우 qna_room 와 room을 조인해서 보여주기
//
//        }

        Room room = entryMapper.getRoomInfoByCode(code);

        return CheckCodeResponse.builder()
                .roomNo(room.getNo())
                .title(room.getTitle())
                .code(room.getCode())
                .capacity(room.getCapacity())
                .hasPassword(room.getPassword() != null)
                .build();



    }

    @Override
    // 입력한 비밀번호와 저장된 방 비밀번호 비교.
    public CheckPasswordResponse verifyRoomPassword(CheckPasswordRequest dto) {

        Room room = entryMapper.selectRoomByNo(dto.getRoomNo());

        if (room == null) {
            throw new RuntimeException("방을 찾을 수 없습니다.");
        }

        if(!passwordEncoder.matches(dto.getPassword(),room.getPassword())){
            log.info("verifyRoomPassword service 방 비밀번호 불일치");
            throw new RuntimeException("방의 비밀번호가 일치하지 않습니다.");
        }

        return new CheckPasswordResponse("비밀번호 일치");
    }

    @Override
    @Transactional
    // 참가자 저장 및 발급된 참가자 번호 반환.
    public InsertParticipantResponse joinRoom(InsertParticipantRequest dto) {

        Participant participant = Participant.builder()
                .roomNo(dto.getRoomNo())
                .nickname(dto.getNickname())
                .build();


        entryMapper.insertParticipant(participant);
        Long participantNo = participant.getNo();

        // 참여자용 랜덤 토큰 생성
        String participantToken = UUID.randomUUID().toString();
        log.info("참여자용 토큰:{}",participantToken);

        return new InsertParticipantResponse(participantNo,"참여자 insert 완료",participantToken);
    }


}

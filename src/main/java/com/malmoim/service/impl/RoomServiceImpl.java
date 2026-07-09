package com.malmoim.service.impl;

import com.malmoim.domain.Member;
import com.malmoim.domain.QnaRoom;
import com.malmoim.domain.Room;
import com.malmoim.dto.room.qna.CreateQnaRoomDto;
import com.malmoim.dto.room.MyRoomsResponseDto;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.RoomMapper;
import com.malmoim.service.RoomService;
import com.malmoim.util.RoomCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper;
    private final MemberMapper memberMapper;
    private final QnaRoomMapper qnaRoomMapper;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void createQnaRoom(CreateQnaRoomDto dto, String hostEmail) {

        //코드 발급,패스워드 null여부

        String code = RoomCodeGenerator.generate();
        log.info("random code:{}", code);

        String encodedPassword = passwordEncoder.encode(dto.getPassword());


        // code가 이미 존재한다면 다시 발급
        while(roomMapper.countByCode(code)>=1){
            code = RoomCodeGenerator.generate();
        }

        //로그인한 사용자(host)의 정보 가져오기
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
                .type("QnA")
                .visibility(dto.getIsChecked() ? "PRIVATE" : "PUBLIC") //체크됨(true => 비공개방)
                .build();

        roomMapper.createRoom(room);

        // room을 상속받는 1:1 구조의 qna_room 생성
        qnaRoomMapper.createQnaRoom(QnaRoom.builder()
                .roomNo(room.getNo())
                .build());


    }

    @Override
    @Transactional
    public MyRoomsResponseDto getMyRooms(String hostEmail, int page, int size) {

        Member host = memberMapper.getMemberByEmail(hostEmail);

        MyRoomsResponseDto dto = new MyRoomsResponseDto();

        int offset = (page - 1) * size;

        dto.setRooms(roomMapper.getMyRooms(host.getNo(), offset, size));
        dto.setTotalCount(roomMapper.countMyRooms(host.getNo()));


        return dto;
    }

    @Override
    public Room getMyOneRoom(long no, String hostEmail) {
        Member host = memberMapper.getMemberByEmail(hostEmail);

        return roomMapper.getMyOneRoom(no, host.getNo());
    }

    @Override
    public Room getOneRoomWithOnlyNo(Long no) {
        return roomMapper.getOneRoomWithOnlyNo(no);
    }
}

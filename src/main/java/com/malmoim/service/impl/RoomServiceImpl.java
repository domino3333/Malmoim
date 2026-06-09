package com.malmoim.service.impl;

import com.malmoim.domain.Member;
import com.malmoim.domain.Room;
import com.malmoim.dto.room.CreateQnaRoomDto;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.mapper.RoomMapper;
import com.malmoim.service.RoomService;
import com.malmoim.util.RoomCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper;
    private final MemberMapper memberMapper;


    @Override
    @Transactional
    public void createQnARoom(CreateQnaRoomDto dto, String hostEmail) {

        //코드 발급,패스워드 null여부

        String code = RoomCodeGenerator.generate();
        log.info("random code:{}", code);

        //로그인한 사용자(host)의 정보 가져오기
        Member host = memberMapper.getMemberByEmail(hostEmail);
        if (host == null) {
            throw new UsernameNotFoundException("host가 없습니다.");
        }


        roomMapper.CreateQnARoom(Room.builder()
                .hostNo(host.getNo())
                .title(dto.getTitle())
                .capacity(dto.getCapacity())
                .password(dto.getPassword())
                .code(code)
                .type("QnA")
                .status(dto.getIsChecked() ? "opened":"closed")
                .build());
    }
}

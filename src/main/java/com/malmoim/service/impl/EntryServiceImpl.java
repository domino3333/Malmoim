package com.malmoim.service.impl;

import com.malmoim.domain.Room;
import com.malmoim.mapper.EntryMapper;
import com.malmoim.service.EntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntryServiceImpl implements EntryService {


    private final EntryMapper entryMapper;



    @Override
    public void checkRoomCode(String code) {

        if(entryMapper.countRoomByCode(code) < 1 ){
            throw new RuntimeException("코드에 해당하는 방이 존재하지 않습니다.");
        }

        //todo 나중엔 방마다 타입이 다를 텐데, 이때는 어떻게 해야 할지 생각해야 함
        Room room = entryMapper.getRoomInfoByCode(code);
        if(room.getType().equals("QnA")){
            //qna방일경우 qna_room 와 room을 조인해서 보여주기
        }


    }
}

package com.malmoim.controller;


import com.malmoim.domain.Room;
import com.malmoim.dto.room.CreateQnaRoomDto;
import com.malmoim.dto.room.MyRoomsResponseDto;
import com.malmoim.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/room")
public class RoomController {


    private final RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<?> createQnARoom(Authentication authentication, @RequestBody CreateQnaRoomDto dto){

        String hostEmail = authentication.getName();
        log.info("ischecked:{}",dto.getIsChecked());
        roomService.createQnARoom(dto,hostEmail);

        return ResponseEntity.ok("방 생성 완료");
    }


    @GetMapping
    public ResponseEntity<?> getMyRoom(Authentication authentication,@RequestParam int page ,@RequestParam int size){


        String hostEmail = authentication.getName();
        MyRoomsResponseDto dto = roomService.getMyRooms(hostEmail,page,size);

        log.info("MyRoomsResponseDto:{}",dto);

        return ResponseEntity.ok(dto);
    }

}

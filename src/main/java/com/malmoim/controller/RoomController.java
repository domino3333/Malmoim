package com.malmoim.controller;


import com.malmoim.dto.room.qna.CreateQnaRoomDto;
import com.malmoim.dto.room.MyRoomsResponseDto;
import com.malmoim.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/room")
public class RoomController {


    private final RoomService roomService;


    @GetMapping
    public ResponseEntity<?> getMyRooms(Authentication authentication,@RequestParam int page ,@RequestParam int size){


        String hostEmail = authentication.getName();
        MyRoomsResponseDto dto = roomService.getMyRooms(hostEmail,page,size);

        log.info("MyRoomsResponseDto:{}",dto);

        return ResponseEntity.ok(dto);
    }

}

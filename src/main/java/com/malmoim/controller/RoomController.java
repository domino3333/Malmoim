package com.malmoim.controller;


import com.malmoim.dto.room.CreateQnaRoomDto;
import com.malmoim.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/room")
public class RoomController {


    private final RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<?> createQnARoom(Authentication authentication, @RequestBody CreateQnaRoomDto dto){

        String hostEmail = authentication.getName();

        roomService.createQnARoom(dto,hostEmail);

        return null;
    }

}

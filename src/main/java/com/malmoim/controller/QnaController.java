package com.malmoim.controller;


import com.malmoim.domain.Room;
import com.malmoim.dto.room.qna.CreateQnaRoomRequest;
import com.malmoim.dto.room.qna.timer.StartTimerRequest;
import com.malmoim.dto.room.qna.timer.StartTimerResponse;
import com.malmoim.dto.room.qna.timer.UpdateRoomStatusRequest;
import com.malmoim.service.QnaRoomService;
import com.malmoim.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/qna")
public class QnaController {


    private final RoomService roomService;
    private final QnaRoomService qnaRoomService;

    @PostMapping("/create")
    public ResponseEntity<?> createQnaRoom(Authentication authentication, @RequestBody CreateQnaRoomRequest dto){

        String hostEmail = authentication.getName();
        roomService.createQnaRoom(dto,hostEmail);

        return ResponseEntity.ok("방 생성 완료");
    }

    @GetMapping("/{no}/host")
    public ResponseEntity<?> getHostQnaRoom(Authentication authentication,@PathVariable Long no){

        //todo 시작시간, 종료시간 보여줄거면 room과 qna_room을 조인해서 보여주기
        String hostEmail = authentication.getName();
        Room room = roomService.getOwnedRoomByNo(no,hostEmail);

        return ResponseEntity.ok(room);
    }

    @GetMapping("/{no}/participant")
    public ResponseEntity<?> getParticipantQnaRoom(@PathVariable Long no){

        Room room = roomService.getRoomByNo(no);

        return ResponseEntity.ok(room);
    }


    @PostMapping("/{roomNo}/start-timer")
    public ResponseEntity<?> startQuestionPhase(Authentication authentication, @RequestBody StartTimerRequest dto, @PathVariable long roomNo){

        String hostEmail = authentication.getName();
        StartTimerResponse response = qnaRoomService.startQuestionPhase(hostEmail,dto.getDurationSeconds(),roomNo);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{roomNo}/update-status")
    public ResponseEntity<?> updateStatus(Authentication authentication,@PathVariable long roomNo ,@RequestBody UpdateRoomStatusRequest request){

        String hostEmail = authentication.getName();
        qnaRoomService.updateRoomStatus(hostEmail,roomNo,request.getStatus());


        return ResponseEntity.ok("업데이트 완료");
    }


}

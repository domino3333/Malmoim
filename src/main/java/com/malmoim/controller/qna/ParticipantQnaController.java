package com.malmoim.controller.qna;

import com.malmoim.domain.Room;
import com.malmoim.dto.qna.ParticipantInfoResponse;
import com.malmoim.security.ParticipantPrincipal;
import com.malmoim.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/qna")
public class ParticipantQnaController {

    private final RoomService roomService;

    @GetMapping("/{no}/participant")
    // 참가자가 입장한 Q&A 방 정보 조회
    public ResponseEntity<?> getParticipantQnaRoom(@PathVariable Long no) {
        Room room = roomService.getRoomByNo(no);

        return ResponseEntity.ok(room);
    }

    @GetMapping("/participant-info")
    public ResponseEntity<?> getParticipantInfo(Authentication authentication) {
        ParticipantPrincipal participant = (ParticipantPrincipal) authentication.getPrincipal();

        Long participantNo = participant.getParticipantNo();
        log.info("participantNo:{}", participantNo);
        String nickname = participant.getNickname();

        ParticipantInfoResponse response = new ParticipantInfoResponse(nickname);

        return ResponseEntity.ok(response);
    }
}

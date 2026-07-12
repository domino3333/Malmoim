package com.malmoim.controller.entry;


import com.malmoim.dto.entry.*;
import com.malmoim.service.EntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/entry")
public class EntryController {

    private final EntryService entryService;

    @PostMapping("/check-code")
    public ResponseEntity<?> getRoomEntryInfo(@RequestBody CheckCodeRequest dto){

        CheckCodeResponse response = entryService.getRoomEntryInfo(dto.getCode());
        return ResponseEntity.ok(response);

    }


    @PostMapping("/check-password")
    public ResponseEntity<?> verifyRoomPassword(@RequestBody CheckPasswordRequest dto){

        CheckPasswordResponse response = entryService.verifyRoomPassword(dto);
        return ResponseEntity.ok(response);

    }


    @PostMapping("/insert-participant")
    public ResponseEntity<?> joinRoom(@RequestBody InsertParticipantRequest dto){

        InsertParticipantResponse response = entryService.joinRoom(dto);
        return ResponseEntity.ok(response);

    }

}

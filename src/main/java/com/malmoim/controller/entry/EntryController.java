package com.malmoim.controller.entry;


import com.malmoim.dto.entry.CheckCodeRequest;
import com.malmoim.dto.entry.CheckCodeResponse;
import com.malmoim.dto.entry.CheckPasswordRequest;
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
    public ResponseEntity<?> checkRoomCode(@RequestBody CheckCodeRequest dto){

        CheckCodeResponse response = entryService.checkRoomCode(dto.getCode());
        return ResponseEntity.ok(response);

    }


    @PostMapping("/check-password")
    public ResponseEntity<?> checkRoomPassword(@RequestBody CheckPasswordRequest dto){

        //CheckCodeResponse response = entryService.(dto.getPassword());
        return null;

    }

}

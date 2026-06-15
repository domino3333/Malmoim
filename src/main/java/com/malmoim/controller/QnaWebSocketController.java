package com.malmoim.controller;


import com.malmoim.dto.room.qna.TestMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class QnaWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/qna/test")
    public void test(TestMessageDto dto){
        messagingTemplate.convertAndSend(
                "/topic/qna/" + dto.getRoomNo(),
                dto
        );
    }
}

package com.malmoim.controller;


import com.malmoim.dto.room.qna.QnaQuestionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QnaWebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/qna/register")
    // 참여자가 보낸 질문을 같은 방의 모든 구독자에게 전달.
    public void broadcastQuestion(QnaQuestionMessage dto) {

        log.info("websocket server dto:{} ", dto.getQuestion());

        simpMessagingTemplate.convertAndSend("/topic/qna/" + dto.getRoomNo(), dto);

    }


}

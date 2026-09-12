package com.malmoim.controller.qna;

import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.domain.Question;
import com.malmoim.dto.qna.question.QuestionCreatedMessage;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.security.ParticipantPrincipal;
import com.malmoim.service.qna.QnaPresenceService;
import com.malmoim.service.qna.QnaRoomService;
import com.malmoim.service.qna.VoteService;
import com.malmoim.service.qna.impl.QuestionServiceImpl;
import com.malmoim.service.room.RoomService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ParticipantQuestionSubmissionTest {

    private AnnotationConfigApplicationContext context;
    private QuestionMapper questionMapper;
    private QnaRoomMapper qnaRoomMapper;
    private SimpMessagingTemplate messagingTemplate;
    private QnaRoom room;
    private MockMvc mvc;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        questionMapper = mock(QuestionMapper.class);
        qnaRoomMapper = mock(QnaRoomMapper.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);
        room = QnaRoom.builder().roomNo(43L).status(QnaPhase.QUESTION_OPEN)
                .questionEndedAt(LocalDateTime.now().plusMinutes(5)).build();
        when(qnaRoomMapper.selectQnaRoomByRoomNo(43L)).thenReturn(room);
        doAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            question.setNo(50L);
            return null;
        }).when(questionMapper).insertQuestion(any(Question.class));

        ParticipantPrincipal participant = new ParticipantPrincipal(43L, 99L, "guest");
        authentication = new UsernamePasswordAuthenticationToken(
                participant, null, participant.getAuthorities());

        context = new AnnotationConfigApplicationContext();
        context.registerBean(QuestionMapper.class, () -> questionMapper);
        context.registerBean(QnaRoomMapper.class, () -> qnaRoomMapper);
        context.registerBean(SimpMessagingTemplate.class, () -> messagingTemplate);
        context.registerBean(RoomService.class, () -> mock(RoomService.class));
        context.registerBean(QnaRoomService.class, () -> mock(QnaRoomService.class));
        context.registerBean(QnaPresenceService.class, () -> mock(QnaPresenceService.class));
        context.registerBean(VoteService.class, () -> mock(VoteService.class));
        context.register(QuestionServiceImpl.class, ParticipantQnaController.class);
        context.refresh();
        mvc = MockMvcBuilders.standaloneSetup(context.getBean(ParticipantQnaController.class)).build();
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void createsQuestionAndBroadcastsSavedDataUsingAuthenticatedRoom() throws Exception {
        mvc.perform(post("/api/participant/qna/questions").principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roomNo\":999,\"question\":\"A question\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionNo").value(50))
                .andExpect(jsonPath("$.roomNo").value(43))
                .andExpect(jsonPath("$.participantNo").value(99))
                .andExpect(jsonPath("$.nickname").value("guest"))
                .andExpect(jsonPath("$.content").value("A question"))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.voteCount").value(0))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        var saved = ArgumentCaptor.forClass(Question.class);
        var broadcast = ArgumentCaptor.forClass(QuestionCreatedMessage.class);
        var order = inOrder(questionMapper, messagingTemplate);
        order.verify(questionMapper).insertQuestion(saved.capture());
        order.verify(messagingTemplate).convertAndSend(eq("/topic/qna/43"), broadcast.capture());
        assertThat(saved.getValue().getRoomNo()).isEqualTo(43L);
        assertThat(saved.getValue().getParticipantNo()).isEqualTo(99L);
        assertThat(broadcast.getValue().getQuestionNo()).isEqualTo(50L);
        assertThat(broadcast.getValue().getContent()).isEqualTo("A question");
        assertThat(broadcast.getValue().getNickname()).isEqualTo("guest");
        verifyNoMoreInteractions(messagingTemplate);
    }

    @Test
    void closedQuestionPhaseDoesNotInsertOrBroadcast() {
        room.setStatus(QnaPhase.QUESTION_CLOSED);
        assertRegistrationRejected();
        verifyNoInteractions(questionMapper, messagingTemplate);
    }

    @Test
    void expiredQuestionDeadlineDoesNotInsertOrBroadcast() {
        room.setQuestionEndedAt(LocalDateTime.now().minusSeconds(1));
        assertRegistrationRejected();
        verifyNoInteractions(questionMapper, messagingTemplate);
    }

    @Test
    void failedInsertDoesNotBroadcastSuccess() {
        doThrow(new IllegalStateException("Database unavailable"))
                .when(questionMapper).insertQuestion(any(Question.class));
        assertThatThrownBy(() -> mvc.perform(post("/api/participant/qna/questions")
                .principal(authentication).contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\":\"A question\"}")))
                .hasRootCauseInstanceOf(IllegalStateException.class);
        verifyNoInteractions(messagingTemplate);
    }

    private void assertRegistrationRejected() {
        assertThatThrownBy(() -> mvc.perform(post("/api/participant/qna/questions")
                .principal(authentication).contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\":\"A question\"}")))
                .hasRootCauseInstanceOf(RuntimeException.class);
    }
}

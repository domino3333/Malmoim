package com.malmoim.controller.qna;

import com.malmoim.domain.Member;
import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.dto.qna.phase.QnaPhaseResponse;
import com.malmoim.dto.qna.question.QuestionResponse;
import com.malmoim.dto.qna.room.QnaRoomInfoResponse;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.mapper.RoomMapper;
import com.malmoim.security.ParticipantPrincipal;
import com.malmoim.service.qna.QnaPresenceService;
import com.malmoim.service.qna.VoteService;
import com.malmoim.service.qna.impl.QnaRoomServiceImpl;
import com.malmoim.service.qna.impl.QuestionServiceImpl;
import com.malmoim.service.room.impl.RoomServiceImpl;
import com.malmoim.websocket.qna.QnaPresenceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HostRoomOwnershipTest {

    private static final long ROOM_NO = 43L;
    private static final String OWNER = "owner@example.test";
    private static final String OTHER_HOST = "other@example.test";

    private AnnotationConfigApplicationContext context;
    private MemberMapper memberMapper;
    private RoomMapper roomMapper;
    private QnaRoomMapper qnaRoomMapper;
    private QuestionMapper questionMapper;
    private QnaPresenceRegistry registry;
    private SimpMessagingTemplate messagingTemplate;
    private QnaRoom qnaRoom;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        memberMapper = mock(MemberMapper.class);
        roomMapper = mock(RoomMapper.class);
        qnaRoomMapper = mock(QnaRoomMapper.class);
        questionMapper = mock(QuestionMapper.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);
        registry = spy(new QnaPresenceRegistry());
        registry.connect("session-1", ROOM_NO, 99L, "guest");
        clearInvocations(registry);

        when(memberMapper.getMemberByEmail(OWNER))
                .thenReturn(Member.builder().no(7L).email(OWNER).build());
        when(memberMapper.getMemberByEmail(OTHER_HOST))
                .thenReturn(Member.builder().no(8L).email(OTHER_HOST).build());
        when(roomMapper.existsByRoomNoAndHostNo(ROOM_NO, 7L)).thenReturn(1);
        when(roomMapper.existsByRoomNoAndHostNo(ROOM_NO, 8L)).thenReturn(0);

        QnaRoomInfoResponse room = new QnaRoomInfoResponse();
        room.setRoomNo(ROOM_NO);
        room.setHostNo(7L);
        room.setTitle("Test room");
        room.setStatus(QnaPhase.READY);
        when(roomMapper.selectRoomByNoAndHostNo(ROOM_NO, 7L)).thenReturn(room);
        when(roomMapper.selectRoomByNo(ROOM_NO)).thenReturn(room);

        qnaRoom = QnaRoom.builder().roomNo(ROOM_NO).status(QnaPhase.VOTING_CLOSED).build();
        when(qnaRoomMapper.selectQnaRoomByRoomNo(ROOM_NO)).thenReturn(qnaRoom);
        doAnswer(invocation -> {
            qnaRoom.setStatus(invocation.getArgument(1));
            return 1;
        }).when(qnaRoomMapper).updateQnaPhase(eq(ROOM_NO), any(QnaPhase.class));

        LocalDateTime startedAt = LocalDateTime.of(2026, 9, 3, 12, 0);
        when(qnaRoomMapper.selectQuestionPhaseByRoomNo(ROOM_NO)).thenReturn(
                new QnaPhaseResponse(ROOM_NO, QnaPhase.QUESTION_OPEN, startedAt, startedAt.plusMinutes(5)));
        when(qnaRoomMapper.selectVotingPhaseByRoomNo(ROOM_NO)).thenReturn(
                new QnaPhaseResponse(ROOM_NO, QnaPhase.VOTING_OPEN, startedAt, startedAt.plusMinutes(5)));
        when(questionMapper.getQuestionList(ROOM_NO)).thenReturn(List.of(
                QuestionResponse.builder().questionNo(10L).participantNo(99L).nickname("guest")
                        .content("A question").roomNo(ROOM_NO).voteCount(0)
                        .createdAt(startedAt).status("WAITING").build()));
        when(questionMapper.getSortedQuestionListByRoomNo(ROOM_NO)).thenReturn(List.of());

        context = new AnnotationConfigApplicationContext();
        context.registerBean(MemberMapper.class, () -> memberMapper);
        context.registerBean(RoomMapper.class, () -> roomMapper);
        context.registerBean(QnaRoomMapper.class, () -> qnaRoomMapper);
        context.registerBean(QuestionMapper.class, () -> questionMapper);
        context.registerBean(QnaPresenceRegistry.class, () -> registry);
        context.registerBean(SimpMessagingTemplate.class, () -> messagingTemplate);
        context.registerBean(PasswordEncoder.class, () -> new BCryptPasswordEncoder());
        context.registerBean(VoteService.class, () -> mock(VoteService.class));
        context.register(RoomServiceImpl.class, QnaRoomServiceImpl.class, QuestionServiceImpl.class,
                QnaPresenceService.class, HostQnaController.class, ParticipantQnaController.class);
        context.scan("com.malmoim.controller.common");
        context.refresh();

        mvc = MockMvcBuilders.standaloneSetup(context.getBean(HostQnaController.class),
                        context.getBean(ParticipantQnaController.class))
                .setControllerAdvice(context.getBeansWithAnnotation(ControllerAdvice.class).values().toArray())
                .build();
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"host", "participant-list", "question-list", "start-timer",
            "start-voting", "update-status", "start-answering"})
    void otherHostIsRejectedBeforeReadingOrChangingRoomData(String endpoint) throws Exception {
        mvc.perform(hostRequest(endpoint).principal(host(OTHER_HOST)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(qnaRoomMapper, questionMapper, messagingTemplate);
        verify(registry, never()).getActiveParticipants(any());
        assertThat(qnaRoom.getStatus()).isEqualTo(QnaPhase.VOTING_CLOSED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"host", "participant-list", "question-list", "start-timer",
            "start-voting", "update-status", "start-answering"})
    void missingMemberIsRejectedWithoutDereferencingNull(String endpoint) throws Exception {
        mvc.perform(hostRequest(endpoint).principal(host("missing@example.test")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(roomMapper, qnaRoomMapper, questionMapper, messagingTemplate);
        verify(registry, never()).getActiveParticipants(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"host", "participant-list", "question-list", "start-timer",
            "start-voting", "update-status", "start-answering"})
    void ownerKeepsExistingSuccessfulResponseShape(String endpoint) throws Exception {
        if (endpoint.equals("start-voting")) {
            qnaRoom.setStatus(QnaPhase.QUESTION_CLOSED);
        }
        var result = mvc.perform(hostRequest(endpoint).principal(host(OWNER)))
                .andExpect(status().isOk());

        switch (endpoint) {
            case "host" -> result.andExpect(jsonPath("$.roomNo").value(43));
            case "participant-list" -> result.andExpect(jsonPath("$.participantCount").value(1))
                    .andExpect(jsonPath("$.participants[0].nickname").value("guest"));
            case "question-list" -> result.andExpect(jsonPath("$[0].questionNo").value(10));
            case "start-timer" -> result.andExpect(jsonPath("$.status").value("QUESTION_OPEN"));
            case "start-voting" -> result.andExpect(jsonPath("$.status").value("VOTING_OPEN"));
            case "update-status" -> result.andExpect(jsonPath("$.status").value("QUESTION_CLOSED"));
            case "start-answering" -> result.andExpect(jsonPath("$.qnaPhaseResponse.status").value("ANSWERING"))
                    .andExpect(jsonPath("$.questions").isArray());
        }
        verify(roomMapper, atLeastOnce()).existsByRoomNoAndHostNo(ROOM_NO, 7L);
    }

    @Test
    void participantQuestionListDoesNotRequireHostOwnership() throws Exception {
        mvc.perform(get("/api/participant/qna/question-list").principal(participant()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("A question"));

        verifyNoInteractions(memberMapper, roomMapper);
    }

    @Test
    void participantPresenceSnapshotDoesNotRequireHostOwnership() throws Exception {
        mvc.perform(get("/api/participant/qna/participant-list").principal(participant()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participantCount").value(1))
                .andExpect(jsonPath("$.participants[0].participantNo").value(99));

        verifyNoInteractions(memberMapper, roomMapper);
    }

    private MockHttpServletRequestBuilder hostRequest(String endpoint) {
        String url = "/api/host/qna/" + ROOM_NO + "/" + endpoint;
        return switch (endpoint) {
            case "start-timer", "start-voting" -> post(url).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"durationSeconds\":300}");
            case "update-status" -> post(url).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"QUESTION_CLOSED\"}");
            case "start-answering" -> post(url);
            default -> get(url);
        };
    }

    private Authentication host(String email) {
        return new UsernamePasswordAuthenticationToken(email, null, List.of());
    }

    private Authentication participant() {
        ParticipantPrincipal participant = new ParticipantPrincipal(ROOM_NO, 99L, "guest");
        return new UsernamePasswordAuthenticationToken(participant, null, participant.getAuthorities());
    }
}

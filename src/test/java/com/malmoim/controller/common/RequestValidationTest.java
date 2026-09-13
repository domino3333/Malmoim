package com.malmoim.controller.common;

import com.malmoim.controller.entry.EntryController;
import com.malmoim.controller.qna.HostQnaController;
import com.malmoim.controller.qna.ParticipantQnaController;
import com.malmoim.domain.*;
import com.malmoim.mapper.*;
import com.malmoim.security.ParticipantPrincipal;
import com.malmoim.security.jwt.JwtTokenProvider;
import com.malmoim.service.entry.impl.EntryServiceImpl;
import com.malmoim.service.qna.QnaPresenceService;
import com.malmoim.service.qna.VoteService;
import com.malmoim.service.qna.impl.QnaRoomServiceImpl;
import com.malmoim.service.qna.impl.QuestionServiceImpl;
import com.malmoim.service.room.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RequestValidationTest {

    private static final String CREATE = "/api/host/qna/create";
    private static final String JOIN = "/api/entry/insert-participant";
    private static final String VERIFY = "/api/entry/check-password";
    private static final String QUESTION = "/api/participant/qna/questions";
    private static final String OWNER = "owner@example.test";
    private static final Map<String, Object> CREATE_BODY = Map.of(
            "title", "Test room", "capacity", 10, "isPrivate", false);
    private static final Map<String, Object> JOIN_BODY = Map.of("roomNo", 43L, "nickname", "guest");
    private static final Map<String, Object> VERIFY_BODY = Map.of("roomNo", 43L, "password", "secret");
    private static final JsonMapper JSON = JsonMapper.builder().build();

    private RoomMapper roomMapper;
    private QnaRoomMapper qnaRoomMapper;
    private ParticipantMapper participantMapper;
    private QuestionMapper questionMapper;
    private JwtTokenProvider tokenProvider;
    private BCryptPasswordEncoder passwordEncoder;
    private SimpMessagingTemplate messagingTemplate;
    private Room room;
    private MockMvc mvc;
    private Authentication participant;

    @BeforeEach
    void setUp() {
        roomMapper = mock(RoomMapper.class);
        qnaRoomMapper = mock(QnaRoomMapper.class);
        participantMapper = mock(ParticipantMapper.class);
        questionMapper = mock(QuestionMapper.class);
        tokenProvider = mock(JwtTokenProvider.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);
        passwordEncoder = spy(new BCryptPasswordEncoder(4));
        room = Room.builder().no(43L).visibility("PRIVATE").password(passwordEncoder.encode("")).build();
        clearInvocations(passwordEncoder);
        when(roomMapper.selectRoomForPasswordVerification(43L)).thenReturn(room);
        when(qnaRoomMapper.selectQnaRoomByRoomNo(43L)).thenReturn(QnaRoom.builder()
                .roomNo(43L).status(QnaPhase.QUESTION_OPEN)
                .questionEndedAt(LocalDateTime.now().plusMinutes(5)).build());
        MemberMapper memberMapper = mock(MemberMapper.class);
        when(memberMapper.getMemberByEmail(OWNER)).thenReturn(Member.builder().no(7L).build());
        doAnswer(invocation -> {
            Room saved = invocation.getArgument(0);
            saved.setNo(44L);
            return null;
        }).when(roomMapper).insertRoom(any(Room.class));
        doAnswer(invocation -> {
            Participant saved = invocation.getArgument(0);
            saved.setNo(99L);
            return null;
        }).when(participantMapper).insertParticipant(any(Participant.class));
        doAnswer(invocation -> {
            Question saved = invocation.getArgument(0);
            saved.setNo(50L);
            return null;
        }).when(questionMapper).insertQuestion(any(Question.class));
        when(tokenProvider.createParticipantToken(99L, 43L, "guest")).thenReturn("participant-token");

        RoomService roomService = mock(RoomService.class);
        QuestionServiceImpl questionService = new QuestionServiceImpl(questionMapper, qnaRoomMapper, roomService);
        QnaRoomServiceImpl qnaRoomService = new QnaRoomServiceImpl(qnaRoomMapper, memberMapper,
                participantMapper, roomMapper, passwordEncoder, roomService, questionService);
        EntryServiceImpl entryService = new EntryServiceImpl(roomMapper, participantMapper, passwordEncoder, tokenProvider);
        QnaPresenceService presenceService = mock(QnaPresenceService.class);
        mvc = MockMvcBuilders.standaloneSetup(
                        new EntryController(entryService),
                        new HostQnaController(qnaRoomService, messagingTemplate, presenceService, questionService),
                        new ParticipantQnaController(qnaRoomService, presenceService, questionService,
                                mock(VoteService.class), messagingTemplate))
                .setControllerAdvice(new ApiExceptionHandler()).build();
        ParticipantPrincipal principal = new ParticipantPrincipal(43L, 99L, "guest");
        participant = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void invalidFieldsReturn400WithMessageWithoutSaving(String path, Map<String, Object> body) throws Exception {
        request(path, body).andExpect(status().isBadRequest())
                .andExpect(content().string(not(emptyOrNullString())));
        assertNothingSaved();
    }

    static Stream<Arguments> invalidRequests() {
        List<Arguments> cases = new ArrayList<>();
        for (String value : new String[]{null, "", " \t\n"}) {
            cases.add(Arguments.of(CREATE, with(CREATE_BODY, "title", value)));
            cases.add(Arguments.of(JOIN, with(JOIN_BODY, "nickname", value)));
            cases.add(Arguments.of(QUESTION, with(Map.of(), "question", value)));
            cases.add(Arguments.of(VERIFY, with(VERIFY_BODY, "password", value)));
        }
        for (Integer value : new Integer[]{null, 0, -1}) {
            cases.add(Arguments.of(CREATE, with(CREATE_BODY, "capacity", value)));
            cases.add(Arguments.of(JOIN, with(JOIN_BODY, "roomNo", value)));
            cases.add(Arguments.of(VERIFY, with(VERIFY_BODY, "roomNo", value)));
        }
        cases.add(Arguments.of(CREATE, with(CREATE_BODY, "isPrivate", null)));
        cases.add(Arguments.of(CREATE, with(CREATE_BODY, "title", "a".repeat(101))));
        cases.add(Arguments.of(JOIN, with(JOIN_BODY, "nickname", "a".repeat(21))));
        cases.add(Arguments.of(QUESTION, Map.of("question", "a".repeat(1001))));
        for (String path : List.of(CREATE, JOIN, VERIFY, QUESTION)) {
            cases.add(Arguments.of(path, Map.of()));
        }
        return cases.stream();
    }

    @ParameterizedTest
    @MethodSource("invalidPrivatePasswords")
    void privatePasswordsAreRejectedBeforeEncodingMatchingOrSaving(String path, String password) throws Exception {
        Map<String, Object> body = path.equals(CREATE)
                ? with(CREATE_BODY, "isPrivate", true) : path.equals(JOIN) ? JOIN_BODY : VERIFY_BODY;
        request(path, with(body, "password", password)).andExpect(status().isBadRequest())
                .andExpect(content().string(not(emptyOrNullString())));
        verifyNoInteractions(passwordEncoder);
        assertNothingSaved();
    }

    static Stream<Arguments> invalidPrivatePasswords() {
        List<Arguments> cases = new ArrayList<>();
        for (String path : List.of(CREATE, JOIN, VERIFY)) {
            for (String password : new String[]{null, "", " \t\n", "a".repeat(73), "가".repeat(25)}) {
                cases.add(Arguments.of(path, password));
            }
        }
        return cases.stream();
    }

    @ParameterizedTest
    @MethodSource("publicPasswords")
    void publicCreationStoresNoPassword(String password) throws Exception {
        request(CREATE, with(CREATE_BODY, "password", password)).andExpect(status().isOk());
        var saved = ArgumentCaptor.forClass(Room.class);
        verify(roomMapper).insertRoom(saved.capture());
        assertThat(saved.getValue().getPassword()).isNull();
        assertThat(saved.getValue().getVisibility()).isEqualTo("PUBLIC");
        verifyNoInteractions(passwordEncoder);
    }

    static Stream<Arguments> publicPasswords() {
        return Stream.of(Arguments.of((Object) null), Arguments.of(""), Arguments.of("secret"));
    }

    @ParameterizedTest
    @MethodSource("validPrivatePasswords")
    void validPrivatePasswordCanCreateVerifyAndJoinWithoutTrimming(String password) throws Exception {
        request(CREATE, with(with(CREATE_BODY, "isPrivate", true), "password", password))
                .andExpect(status().isOk());
        var saved = ArgumentCaptor.forClass(Room.class);
        verify(roomMapper).insertRoom(saved.capture());
        assertThat(saved.getValue().getPassword()).isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, saved.getValue().getPassword())).isTrue();
        room.setPassword(saved.getValue().getPassword());
        request(VERIFY, with(VERIFY_BODY, "password", password)).andExpect(status().isOk());
        request(JOIN, with(JOIN_BODY, "password", password)).andExpect(status().isOk())
                .andExpect(jsonPath("$.participantNo").value(99));
        verify(tokenProvider).createParticipantToken(99L, 43L, "guest");
        verify(passwordEncoder).encode(password);
    }

    static Stream<String> validPrivatePasswords() {
        return Stream.of("a".repeat(72), "가".repeat(24), " secret ");
    }

    @Test
    void publicRoomCanBeJoinedWithoutPassword() throws Exception {
        room.setVisibility("PUBLIC");
        room.setPassword(null);
        request(JOIN, JOIN_BODY).andExpect(status().isOk())
                .andExpect(jsonPath("$.participantNo").value(99));
        verifyNoInteractions(passwordEncoder);
        verify(tokenProvider).createParticipantToken(99L, 43L, "guest");
    }

    @Test
    void maximumLengthFieldsRemainAcceptedAndQuestionUsesAuthenticatedRoom() throws Exception {
        request(CREATE, with(CREATE_BODY, "title", "a".repeat(100))).andExpect(status().isOk());
        room.setVisibility("PUBLIC");
        request(JOIN, with(JOIN_BODY, "nickname", "a".repeat(20))).andExpect(status().isOk());
        request(QUESTION, Map.of("question", "a".repeat(1000))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomNo").value(43))
                .andExpect(jsonPath("$.content").value("a".repeat(1000)));
    }

    private ResultActions request(String path, Map<String, Object> body) throws Exception {
        Authentication authentication = path.startsWith("/api/host/")
                ? new UsernamePasswordAuthenticationToken(OWNER, null, List.of()) : participant;
        return mvc.perform(post(path).principal(authentication).contentType(MediaType.APPLICATION_JSON)
                .content(JSON.writeValueAsString(body)));
    }

    @Test
    void missingEntryCodeReturns404WithMessage() throws Exception {
        request("/api/entry/check-code", Map.of("code", "ABSENT"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(not(emptyOrNullString())));
        assertNothingSaved();
    }

    @ParameterizedTest
    @MethodSource("entryRequests")
    void missingRoomReturns404WithoutIssuingParticipantToken(String path, Map<String, Object> body) throws Exception {
        when(roomMapper.selectRoomForPasswordVerification(43L)).thenReturn(null);
        request(path, body).andExpect(status().isNotFound())
                .andExpect(content().string(not(emptyOrNullString())));
        assertNothingSaved();
    }

    @ParameterizedTest
    @MethodSource("entryRequests")
    void wrongPasswordReturns400WithoutIssuingParticipantToken(String path, Map<String, Object> body) throws Exception {
        request(path, body).andExpect(status().isBadRequest())
                .andExpect(content().string(not(emptyOrNullString())));
        assertNothingSaved();
    }

    static Stream<Arguments> entryRequests() {
        return Stream.of(Arguments.of(VERIFY, VERIFY_BODY),
                Arguments.of(JOIN, with(JOIN_BODY, "password", "wrong-password")));
    }

    @Test
    void votingStartInWrongPhaseReturns409WithoutUpdatesOrBroadcast() throws Exception {
        request("/api/host/qna/43/start-voting", Map.of("durationSeconds", 300))
                .andExpect(status().isConflict())
                .andExpect(content().string(not(emptyOrNullString())));
        verify(qnaRoomMapper, never()).updateVotingPeriod(anyLong(), any(), any());
        verify(qnaRoomMapper, never()).updateQnaPhase(anyLong(), any());
        verifyNoInteractions(messagingTemplate);
    }

    private void assertNothingSaved() {
        verify(roomMapper, never()).insertRoom(any(Room.class));
        verify(qnaRoomMapper, never()).insertQnaRoom(any(QnaRoom.class));
        verifyNoInteractions(participantMapper, questionMapper, tokenProvider, messagingTemplate);
    }

    private static Map<String, Object> with(Map<String, Object> body, String field, Object value) {
        Map<String, Object> copy = new HashMap<>(body);
        copy.put(field, value);
        return copy;
    }
}

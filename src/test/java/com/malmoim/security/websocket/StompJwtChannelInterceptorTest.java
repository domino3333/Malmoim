package com.malmoim.security.websocket;

import com.malmoim.domain.Room;
import com.malmoim.mapper.ParticipantMapper;
import com.malmoim.mapper.RoomMapper;
import com.malmoim.security.MemberPrincipal;
import com.malmoim.security.MemberUserDetailsService;
import com.malmoim.security.jwt.JwtTokenProvider;
import com.malmoim.service.room.RoomService;
import com.malmoim.websocket.qna.QnaPresenceRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class StompJwtChannelInterceptorTest {

    private final JwtTokenProvider tokens = mock(JwtTokenProvider.class);
    private final MemberUserDetailsService members = mock(MemberUserDetailsService.class);
    private final RoomService roomService = mock(RoomService.class);
    private final RoomMapper rooms = mock(RoomMapper.class);
    private final ParticipantMapper participants = mock(ParticipantMapper.class);
    private final QnaPresenceRegistry presence = new QnaPresenceRegistry();
    private final StompJwtChannelInterceptor interceptor = new StompJwtChannelInterceptor(
            tokens, members, roomService, rooms, participants, presence
    );
    private final MessageChannel channel = mock(MessageChannel.class);

    @Test
    void participantConnectRejectsRoomFullWithoutAddingPresence() {
        when(tokens.validateToken("token-a")).thenReturn(true);
        when(tokens.extractType("token-a")).thenReturn("PARTICIPANT");
        when(tokens.extractRoomNo("token-a")).thenReturn(1L);
        when(tokens.extractParticipantNo("token-a")).thenReturn(10L);
        when(tokens.extractNickname("token-a")).thenReturn("A");
        when(tokens.validateToken("token-b")).thenReturn(true);
        when(tokens.extractType("token-b")).thenReturn("PARTICIPANT");
        when(tokens.extractRoomNo("token-b")).thenReturn(1L);
        when(tokens.extractParticipantNo("token-b")).thenReturn(20L);
        when(tokens.extractNickname("token-b")).thenReturn("B");
        when(participants.existsByParticipantNoAndRoomNo(10L, 1L)).thenReturn(1);
        when(participants.existsByParticipantNoAndRoomNo(20L, 1L)).thenReturn(1);
        when(rooms.selectRoomByRoomNo(1L)).thenReturn(Room.builder().no(1L).capacity(1).build());

        interceptor.preSend(connect("session-a", "token-a", StompCommand.CONNECT), channel);
        MessagingException error = assertThrows(MessagingException.class,
                () -> interceptor.preSend(connect("session-b", "token-b", StompCommand.STOMP), channel));

        assertEquals("ROOM_FULL", error.getMessage());
        assertEquals(1, presence.getActiveParticipants(1L).size());
    }

    @Test
    void unsupportedStompVersionIsRejectedBeforeReservingCapacity() {
        when(tokens.validateToken("participant-token")).thenReturn(true);
        when(tokens.extractType("participant-token")).thenReturn("PARTICIPANT");
        when(tokens.extractRoomNo("participant-token")).thenReturn(1L);
        when(tokens.extractParticipantNo("participant-token")).thenReturn(10L);
        when(tokens.extractNickname("participant-token")).thenReturn("A");
        when(participants.existsByParticipantNoAndRoomNo(10L, 1L)).thenReturn(1);
        when(rooms.selectRoomByRoomNo(1L)).thenReturn(Room.builder().no(1L).capacity(1).build());

        Message<byte[]> request = connect("unsupported-session", "participant-token", StompCommand.CONNECT, "9.9");
        assertThrows(MessagingException.class, () -> interceptor.preSend(request, channel));
        assertEquals(0, presence.getActiveParticipants(1L).size());
    }
    @Test
    void hostConnectDoesNotUseParticipantCapacity() {
        when(tokens.validateToken("host-token")).thenReturn(true);
        when(tokens.extractEmail("host-token")).thenReturn("host@example.test");
        when(members.loadUserByUsername("host@example.test")).thenReturn(
                MemberPrincipal.builder().email("host@example.test").authorities(List.of()).build()
        );

        assertDoesNotThrow(() -> interceptor.preSend(connect("host-session", "host-token", StompCommand.CONNECT), channel));
        assertEquals(0, presence.getActiveParticipants(1L).size());
        verifyNoInteractions(rooms, participants);
    }

    private Message<byte[]> connect(String sessionId, String token, StompCommand command) {
        return connect(sessionId, token, command, "1.2");
    }

    private Message<byte[]> connect(String sessionId, String token, StompCommand command, String version) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        if (version != null) {
            accessor.setAcceptVersion(version);
        }
        accessor.setSessionId(sessionId);
        accessor.setNativeHeader("Authorization", "Bearer " + token);
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}

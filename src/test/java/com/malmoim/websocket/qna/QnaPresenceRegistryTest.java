package com.malmoim.websocket.qna;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.MessagingException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QnaPresenceRegistryTest {

    @Test
    void returningParticipantIsRejectedWhenAnotherParticipantFilledTheRoom() {
        QnaPresenceRegistry registry = new QnaPresenceRegistry();

        registry.admit("a-first", 1L, 10L, "A", 2);
        registry.admit("b", 1L, 20L, "B", 2);
        registry.disconnect("a-first");
        registry.admit("c", 1L, 30L, "C", 2);

        MessagingException error = assertThrows(MessagingException.class,
                () -> registry.admit("a-return", 1L, 10L, "A", 2));
        assertEquals("ROOM_FULL", error.getMessage());
        assertEquals(2, registry.getActiveParticipants(1L).size());
    }

    @Test
    void refreshAllowsSecondSessionForAlreadyActiveParticipant() {
        QnaPresenceRegistry registry = new QnaPresenceRegistry();

        registry.admit("a-first", 1L, 10L, "A", 1);
        registry.admit("a-refresh", 1L, 10L, "A", 1);
        registry.disconnect("a-first");

        assertEquals(1, registry.getActiveParticipants(1L).size());
    }

    @Test
    void simultaneousDifferentParticipantsCannotBothFillLastSpot() throws Exception {
        QnaPresenceRegistry registry = new QnaPresenceRegistry();
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<?> first = pool.submit(() -> admitAfterSignal(registry, start, "a", 10L));
            Future<?> second = pool.submit(() -> admitAfterSignal(registry, start, "b", 20L));
            start.countDown();
            first.get();
            second.get();

            assertEquals(1, registry.getActiveParticipants(1L).size());
        } finally {
            pool.shutdownNow();
        }
    }

    private static void admitAfterSignal(QnaPresenceRegistry registry, CountDownLatch start,
                                         String sessionId, Long participantNo) {
        try {
            start.await();
            registry.admit(sessionId, 1L, participantNo, sessionId, 1);
        } catch (MessagingException ignored) {
            // 한 명만 입장 가능한 방에서는 다른 한 명이 거절된다.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        }
    }
}

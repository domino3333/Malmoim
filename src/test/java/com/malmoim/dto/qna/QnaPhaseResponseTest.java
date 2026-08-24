package com.malmoim.dto.qna;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QnaPhaseResponseTest {

    @Test
    void usesGenericPhaseTimeFields() {
        Set<String> fieldNames = Arrays.stream(QnaPhaseResponse.class.getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toSet());

        assertTrue(fieldNames.contains("phaseStartedAt"));
        assertTrue(fieldNames.contains("phaseEndedAt"));
        assertFalse(fieldNames.contains("questionStartedAt"));
        assertFalse(fieldNames.contains("questionEndedAt"));
    }
}

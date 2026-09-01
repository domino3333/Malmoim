package com.malmoim.dto.qna.phase;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnsweringResultResponseTest {

    @Test
    void usesQuestionsAsTheResultListField() {
        Set<String> fieldNames = Arrays.stream(AnsweringResultResponse.class.getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toSet());

        assertTrue(fieldNames.contains("questions"));
        assertFalse(fieldNames.contains("voteResultResponseList"));
    }
}

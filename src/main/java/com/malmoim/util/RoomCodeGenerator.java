package com.malmoim.util;


import java.security.SecureRandom;

public class RoomCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private RoomCodeGenerator() {
    }

    public static String generate() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }
}

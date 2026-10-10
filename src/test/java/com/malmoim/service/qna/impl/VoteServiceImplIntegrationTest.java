package com.malmoim.service.qna.impl;


import com.malmoim.domain.QnaPhase;
import com.malmoim.service.qna.VoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class VoteServiceImplIntegrationTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    VoteService voteService;

    @Test
    @DisplayName("테스트 DB에 연결한다")
    void connectsToTestDatabase() {
        String database = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        assertEquals("malmoim_test", database);
    }

    @Test
    @DisplayName("중복 투표가 실제로 db에서 어떻게 되는지")
    void rejectsDuplicateVoteInDatabase() {

        assertEquals("malmoim_test", jdbcTemplate.queryForObject("SELECT DATABASE()", String.class));

        String hostEmail = "test@example.com";


        jdbcTemplate.update("insert into member(email,password,name,role) values(?,?,?,?)",
                hostEmail, "unused", "테스트유저1", "NORMAL");

        try {

            Long hostNo = jdbcTemplate.queryForObject("select on from member where email = ? ", Long.class, hostEmail);
            String roomCode = "TEST-CODE";

            jdbcTemplate.update("insert into room(host_no,title,code,capacity) values(?,?,?,?)",
                    hostNo, "중복 추천 테스트 방", roomCode, 10);

            try {

                Long roomNo = jdbcTemplate.queryForObject("select no from room where code = ?", Long.class, roomCode);

                LocalDateTime deadline = LocalDateTime.now().plusHours(1);

                jdbcTemplate.update("insert into qna_room(room_no,status,voting_ended_at) values(?,?,?)",
                        roomNo, QnaPhase.VOTING_OPEN, deadline);

                String nickname = "테스트 참여자";

                jdbcTemplate.update("insert into participant(nickname,room_no) values(?,?)", nickname, roomNo);

                try {

                    Long participantNo = jdbcTemplate.queryForObject("select no from participant where room_no=? and nickname = ?", Long.class, roomNo, nickname);

                    jdbcTemplate.update(
                            "INSERT INTO question (participant_no, content, room_no, vote_count, status) "
                                    + "VALUES (?, ?, ?, ?, ?)",
                            participantNo, "중복 추천 테스트 질문", roomNo, 0, "WAITING"
                    );

                    Long questionNo = jdbcTemplate.queryForObject(
                            "select no from question where participant_no = ? and room_no = ?"
                            , Long.class, participantNo, roomNo);
                    

                    try {

                    } finally {

                        jdbcTemplate.update(
                                "DELETE FROM question WHERE room_no = ? AND participant_no = ?",
                                roomNo, participantNo
                        );
                    }


                } finally {

                    jdbcTemplate.update("delete from participant where nickname = ? and room_no = ?", nickname, roomNo);
                }


            } finally {
                jdbcTemplate.update("delete from room where code = ? ", roomCode);
            }


        } finally {
            jdbcTemplate.update("delete from member where email = ?", hostEmail);
        }

    }


}

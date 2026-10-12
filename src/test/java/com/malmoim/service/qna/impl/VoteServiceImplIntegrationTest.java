package com.malmoim.service.qna.impl;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.service.qna.VoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class VoteServiceImplIntegrationTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    VoteService voteService;

    @MockitoSpyBean
    QuestionMapper questionMapper;

    @Test
    @DisplayName("테스트 DB에 연결한다")
    void connectsToTestDatabase() {
        String database = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        assertEquals("malmoim_test", database);
    }

    @Test
    @DisplayName("중복 투표 테스트")
    void rejectsDuplicateVoteInDatabase() {

        assertEquals("malmoim_test", jdbcTemplate.queryForObject("SELECT DATABASE()", String.class));

        String hostEmail = "test@example.com";

        jdbcTemplate.update("insert into member(email,password,name,role) values(?,?,?,?)",
                hostEmail, "unused", "테스트유저1", "NORMAL");

        try {

            Long hostNo = jdbcTemplate.queryForObject("select no from member where email = ? ", Long.class, hostEmail);
            String roomCode = "TEST-CODE";

            jdbcTemplate.update("insert into room(host_no,title,code,capacity) values(?,?,?,?)",
                    hostNo, "중복 추천 테스트 방", roomCode, 10);

            try {

                Long roomNo = jdbcTemplate.queryForObject("select no from room where code = ?", Long.class, roomCode);

                LocalDateTime deadline = LocalDateTime.now().plusHours(1);

                jdbcTemplate.update("insert into qna_room(room_no,status,voting_ended_at) values(?,?,?)",
                        roomNo, "VOTING_OPEN", deadline);

                String nickname = "테스트 참여자";

                jdbcTemplate.update("insert into participant(nickname,room_no) values(?,?)", nickname, roomNo);

                try {

                    Long participantNo = jdbcTemplate.queryForObject("select no from participant where room_no=? and nickname = ?", Long.class, roomNo, nickname);

                    jdbcTemplate.update(
                            "INSERT INTO question (participant_no, content, room_no, vote_count, status) "
                                    + "VALUES (?, ?, ?, ?, ?)",
                            participantNo, "중복 추천 테스트 질문", roomNo, 0, "WAITING"
                    );

                    try {
                        Long questionNo = jdbcTemplate.queryForObject(
                                "select no from question where participant_no = ? and room_no = ?"
                                , Long.class, participantNo, roomNo);

                        voteService.castVote(roomNo, questionNo, participantNo);

                        Integer voteRecordCount = jdbcTemplate
                                .queryForObject("select COUNT(*) from vote where question_no = ? and participant_no = ?",
                                        Integer.class, questionNo, participantNo
                                );

                        //실제 레코드가 1줄만 생겼는지 검사
                        assertEquals(1, voteRecordCount);


                        Integer voteCount = jdbcTemplate
                                .queryForObject("select vote_count from question where no = ?", Integer.class, questionNo);

                        // question에 대해 voteCount가 1만 증가했는지 검사
                        assertEquals(1, voteCount);

                        //같은 참여자가 같은 질문을 다시 추천
                        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> voteService.castVote(roomNo, questionNo, participantNo));
                        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());


                        //중복 추천 거절 후 추천 기록과 추천 수가 각각 1인지 재확인
                        voteRecordCount = jdbcTemplate
                                .queryForObject("select COUNT(*) from vote where question_no = ? and participant_no = ?",
                                        Integer.class, questionNo, participantNo
                                );

                        voteCount = jdbcTemplate
                                .queryForObject("select vote_count from question where no = ?", Integer.class, questionNo);

                        assertEquals(1, voteRecordCount);
                        assertEquals(1, voteCount);

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

    @Test
    @DisplayName("추천이 insert되었다고 해도 추천 수 증가에 실패하면 롤백한다")
    void rollsBackVoteWhenCountUpdateFails() {

        assertEquals("malmoim_test", jdbcTemplate.queryForObject("select database()", String.class));

        String hostEmail = "host@example.com";

        jdbcTemplate.update("insert into member(email,password,name,role) values(?,?,?,?)",
                hostEmail, "password", "홍길동", "NORMAL");

        try {
            Long hostNo = jdbcTemplate.queryForObject(
                    "select no from member where email = ?", Long.class, hostEmail);

            String roomCode = "ROLLBACK_ROOM";
            jdbcTemplate.update("insert into room(host_no,title,code,capacity) values(?,?,?,?)",
                    hostNo, "타이틀", roomCode, 10);

            try {
                Long roomNo = jdbcTemplate.queryForObject(
                        "select no from room where code = ?", Long.class, roomCode);

                // 추천 가능한 Q&A 상태와 마감시간을 준비
                LocalDateTime deadline = LocalDateTime.now().plusHours(1);
                jdbcTemplate.update("insert into qna_room(room_no,status,voting_ended_at) values(?,?,?)",
                        roomNo, "VOTING_OPEN", deadline);

                String nickname = "참여자닉네임";
                jdbcTemplate.update("insert into participant(nickname,room_no) values(?,?)",
                        nickname, roomNo);

                try {
                    // 번호 조회에 실패해도 참여자 정리를 실행할 수 있도록 try 안에서 조회
                    Long participantNo = jdbcTemplate.queryForObject(
                            "select no from participant where nickname = ? and room_no = ?",
                            Long.class, nickname, roomNo);

                    jdbcTemplate.update(
                            "insert into question(participant_no,content,room_no,vote_count,status) values(?,?,?,?,?)",
                            participantNo, "컨텐트", roomNo, 0, "WAITING");

                    try {
                        // 번호 조회에 실패해도 질문 정리를 실행할 수 있도록 try 안에서 조회
                        Long questionNo = jdbcTemplate.queryForObject(
                                "select no from question where participant_no = ? and room_no = ?",
                                Long.class, participantNo, roomNo);

                        doThrow(new RuntimeException("추천 수 증가 실패")).when(questionMapper).incrementVoteCount(questionNo);

                        RuntimeException error = assertThrows(RuntimeException.class, () -> voteService.castVote(roomNo, questionNo, participantNo));
                        assertEquals("추천 수 증가 실패", error.getMessage());

                        //추천 기록이 실제로 롤백됐는지 확인
                        Integer voteRecordCount = jdbcTemplate
                                .queryForObject("select COUNT(*) from vote where question_no = ? and participant_no =?"
                                        , Integer.class, questionNo, participantNo);
                        assertEquals(0, voteRecordCount);

                        // 추천 수도 그대로인지 검사
                        Integer questionRecordVoteCount =
                                jdbcTemplate.queryForObject("select vote_count from question where no = ? and participant_no =?"
                                        , Integer.class, questionNo, participantNo);

                        assertEquals(0, questionRecordVoteCount);


                    } finally {
                        jdbcTemplate.update(
                                "delete from question where room_no = ? and participant_no = ?",
                                roomNo, participantNo);
                    }

                } finally {
                    jdbcTemplate.update("delete from participant where nickname = ? and room_no = ?",
                            nickname, roomNo);
                }

            } finally {
                jdbcTemplate.update("delete from room where code = ? and host_no = ?",
                        roomCode, hostNo);
            }

        } finally {
            jdbcTemplate.update("delete from member where email = ?", hostEmail);
        }
    }


    @Test
    @DisplayName("같은 참여자가 동시에 중복 추천해도 한 번만 저장한다")
    void rejectsConcurrentDuplicateVotesInDatabase() throws InterruptedException{


        assertEquals("malmoim_test", jdbcTemplate.queryForObject("select database()", String.class));

        String hostEmail = "concurrent@example.com";

        jdbcTemplate.update("insert into member(email,password,name,role) values(?,?,?,?)"
                , hostEmail, "password", "동시추천호스트", "NORMAL");


        try {

            Long hostNo = jdbcTemplate.queryForObject("select no from member where email =?", Long.class, hostEmail);
            String code = "code";


            jdbcTemplate.update("insert into room(host_no,title,code,capacity) values(?,?,?,?)", hostNo, "타이틀", code, 10);

            try {
                Long roomNo = jdbcTemplate.queryForObject("select no from room where code = ?", Long.class, code);

                LocalDateTime deadline = LocalDateTime.now().plusHours(1);
                jdbcTemplate.update("insert into qna_room(room_no,status,voting_ended_at) values(?,?,?)",
                        roomNo, "VOTING_OPEN", deadline);


                String nickname = "참여자닉네임";
                jdbcTemplate.update("insert into participant(nickname,room_no) values(?,?)", nickname, roomNo);

                try {

                    Long participantNo = jdbcTemplate.queryForObject("select no from participant where room_no =? and nickname=?"
                            , Long.class, roomNo, nickname);

                    jdbcTemplate.update("insert into question(participant_no,room_no,content,vote_count,status) values(?,?,?,?,?)"
                            , participantNo, roomNo, "content", 0, "WAITING");

                    try {

                        Long questionNo = jdbcTemplate.queryForObject("select no from question where room_no = ? and participant_no = ?",
                                Long.class, roomNo, participantNo);

                        ExecutorService executor = Executors.newFixedThreadPool(2);
                        try{
                            Runnable task1 = () -> {
                                voteService.castVote(roomNo, questionNo, participantNo);
                                System.out.println("task1 실행");
                            };
                            Runnable task2 = () -> {
                                voteService.castVote(roomNo, questionNo, participantNo);
                                System.out.println("task2 실행");
                            };

                            Future<?> result1 = executor.submit(task1);
                            Future<?> result2 = executor.submit(task2);

                            //서비스 호출이 정상 완료되면 ++;
                            int successCount = 0;
                            Throwable failureCause = null;

                            try {
                                result1.get();
                                successCount++;

                            }catch (ExecutionException e) {
                                System.out.println("task1 실패: " );
                                failureCause = e.getCause();

                            }

                            try {
                                result2.get();
                                successCount++;

                            }catch (ExecutionException e) {
                                System.out.println("task2 실패: ");
                                failureCause = e.getCause();
                            }

                            Integer questionVoteCount = jdbcTemplate
                                    .queryForObject("select vote_count from question where no = ? and participant_no = ?"
                                    ,Integer.class,questionNo,participantNo);
                            Integer voteRecordCount = jdbcTemplate.queryForObject("select COUNT(*) from vote where question_no =? and participant_no = ?",
                                    Integer.class,questionNo,participantNo);

                            //좋아요 수는 1, 좋아요 기록은 하나만 남아야 함
                            assertEquals(1,questionVoteCount);
                            assertEquals(1,voteRecordCount);
                            assertEquals(1,successCount);
                            
                            ResponseStatusException error
                                    = assertInstanceOf(ResponseStatusException.class,failureCause);


                        }finally {
                            executor.shutdown();
                        }



                    } finally {

                        jdbcTemplate.update("delete from question where participant_no = ? and room_no = ?", participantNo, roomNo);
                    }
                } finally {
                    jdbcTemplate.update("delete from participant where room_no = ? and nickname= ?", roomNo, nickname);
                }
            } finally {
                jdbcTemplate.update("delete from room where code = ?", code);
            }
        } finally {
            jdbcTemplate.update("delete from member where email=?", hostEmail);
        }


    }


}

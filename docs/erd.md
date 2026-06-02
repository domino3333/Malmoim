# 말모임

## 1. table

member
-no (pk)
-email varchar2(50) (uk)
-password varchar2(100)
-name varchar2(20)
-created_at


participant
-no
-nickname
-created_at
-room_no

constraint participant_room_fk foreign key (room_no) references room(no)

room
-no
-host_no
-title
-code
-capacity
-password
-created_at
-status

constraint room_member_fk foreign key (host_no) references member(no)
constraint room_status_ck check (status IN ('opened','closed'))




question
-no
-participant_no
-content
-room_no
-vote_count
-created_at
-status

constraint question_status_ck check (status IN ('WAITING','ANSWERED','REJECTED','HIDDEN'))
constraint question_room_fk foreign key (room_no) references room(no)
constraint question_member_fk foreign key (participant_no) references participant(no)


vote
-no
-participant_no
-created_at
-question_no

constraint vote_uk unique (participant_no,question_no) // 한 참가자는 한 질문에 한 번만 추천 가능

constraint question_fk foreign key (question_no) references question(no) on delete cascade
constraint vote_member_fk foreign key (participant_no) references participant(no)


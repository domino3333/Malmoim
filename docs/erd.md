# 말모임

## 1. table

member
-no
-email varchar2(50) (pk)
-password varchar2(100)
-name varchar2(20)
-created_at


room
-no
-code
-capacity
-password
-created_at


question
-no
-author
-content
-created_at


vote
-no
-count
-author
-created_at

constraint question_fk foreign key references question(no) on delete cascade



익명 사용자(참여자),
방 호스트
방
방 키?코드?
질문,
추천


# 말모임 ERD

## 1. 테이블 목록

- `member`: 방을 생성하고 관리하는 진행자 회원
- `participant`: 특정 방에 입장한 익명 참가자
- `room`: Q&A가 진행되는 방
- `question`: 참가자가 방에 등록한 질문
- `vote`: 참가자가 질문에 누른 추천

## 2. 테이블 상세

### 2.1 member

| 컬럼명 | 타입 | 키 | 제약 | 설명 |
| --- | --- | --- | --- | --- |
| no | BIGINT | PK |  | 회원 번호 |
| email | VARCHAR(50) | UK | NOT NULL | 회원 이메일 |
| password | VARCHAR(100) |  | NOT NULL | 비밀번호 |
| name | VARCHAR(20) |  | NOT NULL | 회원 이름 |
| created_at | DATETIME |  | NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성일시 |

### 2.2 participant

| 컬럼명 | 타입 | 키 | 제약 | 설명 |
| --- | --- | --- | --- | --- |
| no | BIGINT | PK |  | 참가자 번호 |
| nickname | VARCHAR(20) |  | NOT NULL | 참가자 닉네임 |
| created_at | DATETIME |  | NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성일시 |
| room_no | BIGINT | FK |  | 참가자가 입장한 방 번호 |

```sql
CONSTRAINT participant_room_fk
FOREIGN KEY (room_no) REFERENCES room(no)
```

### 2.3 room

| 컬럼명 | 타입 | 키 | 제약 | 설명 |
| --- | --- | --- | --- | --- |
| no | BIGINT | PK |  | 방 번호 |
| host_no | BIGINT | FK | NOT NULL | 방을 생성한 회원 번호 |
| title | VARCHAR(100) |  | NOT NULL | 방 제목 |
| code | VARCHAR(20) |  | NOT NULL | 방 입장 코드 |
| capacity | INT |  | NOT NULL | 최대 참가 인원 |
| password | VARCHAR(100) |  |  | 방 비밀번호 |
| created_at | DATETIME |  | NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성일시 |
| status | VARCHAR(20) |  | NOT NULL DEFAULT 'closed' | 방 상태 |

```sql
CONSTRAINT room_member_fk
FOREIGN KEY (host_no) REFERENCES member(no)

CONSTRAINT room_status_ck
CHECK (status IN ('opened', 'closed'))
```

### 2.4 question

| 컬럼명 | 타입 | 키 | 제약 | 설명 |
| --- | --- | --- | --- | --- |
| no | BIGINT | PK |  | 질문 번호 |
| participant_no | BIGINT | FK | NOT NULL | 질문 작성 참가자 번호 |
| content | TEXT |  | NOT NULL | 질문 내용 |
| room_no | BIGINT | FK | NOT NULL | 질문이 등록된 방 번호 |
| vote_count | INT |  | NOT NULL DEFAULT 0 | 추천 수 |
| created_at | DATETIME |  | NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성일시 |
| status | VARCHAR(20) |  |  | 질문 상태 |

```sql
CONSTRAINT question_status_ck
CHECK (status IN ('WAITING', 'ANSWERED', 'REJECTED', 'HIDDEN'))

CONSTRAINT question_room_fk
FOREIGN KEY (room_no) REFERENCES room(no)

CONSTRAINT question_participant_fk
FOREIGN KEY (participant_no) REFERENCES participant(no)
```

### 2.5 vote

| 컬럼명 | 타입 | 키 | 제약 | 설명 |
| --- | --- | --- | --- | --- |
| no | BIGINT | PK |  | 추천 번호 |
| participant_no | BIGINT | FK | NOT NULL | 추천한 참가자 번호 |
| created_at | DATETIME |  | NOT NULL DEFAULT CURRENT_TIMESTAMP | 생성일시 |
| question_no | BIGINT | FK | NOT NULL | 추천 대상 질문 번호 |

```sql
CONSTRAINT vote_uk
UNIQUE (participant_no, question_no)

CONSTRAINT vote_question_fk
FOREIGN KEY (question_no) REFERENCES question(no) ON DELETE CASCADE

CONSTRAINT vote_participant_fk
FOREIGN KEY (participant_no) REFERENCES participant(no)
```

## 3. 관계 요약

- `member` 1명은 여러 개의 `room`을 생성할 수 있다.
- `room` 1개에는 여러 명의 `participant`가 입장할 수 있다.
- `room` 1개에는 여러 개의 `question`이 등록될 수 있다.
- `participant` 1명은 여러 개의 `question`을 작성할 수 있다.
- `participant` 1명은 여러 개의 `vote`를 생성할 수 있다.
- `question` 1개는 여러 개의 `vote`를 받을 수 있다.
- 같은 참가자는 같은 질문에 한 번만 추천할 수 있다.

# skala-stock-api
# 📈 SKALA STOCK API

Spring Boot 기반의 **주식 거래 REST API 서버**입니다.  
플레이어가 로그인한 후 주식을 **조회 · 매수 · 매도**할 수 있도록 구현했습니다.

---

## 🛠 Tech Stack

- Java 17+
- Spring Boot 3
- Spring Data JPA
- H2 Database
- JWT (Cookie 기반 인증)
- Swagger UI
- Maven

---

## 📂 Project Structure
src/main/java/com/sk/skala/stockapi
├── controller # API 엔드포인트
├── service # 비즈니스 로직
├── repository # JPA Repository
├── data
│ ├── dto # 요청/응답 DTO
│ ├── table # JPA Entity
│ └── common # 공통 Response / PagedList
├── exception # 커스텀 예외 & GlobalExceptionHandler
└── config # 상수, 에러 코드 등


---

## ✨ 주요 기능

### 1️⃣ 주식(Stock)
- 전체 주식 목록 조회 (페이징)
- 주식 등록 / 수정 / 삭제

### 2️⃣ 플레이어(Player)
- 플레이어 생성(회원가입)
- 플레이어 로그인
- 플레이어 정보 조회 (보유 주식 포함)
- 플레이어 삭제

### 3️⃣ 인증 / 세션
- 로그인 성공 시 **JWT를 쿠키로 발급**
- 이후 요청에서 쿠키 기반 사용자 인증
- 로그인 없이 매수/매도 요청 시 `SESSION_NOT_FOUND` 에러 반환

### 4️⃣ 주식 거래
- 주식 매수
  - 잔액 검증
  - 보유 주식 없으면 생성, 있으면 수량 증가
- 주식 매도
  - 보유 수량 검증
  - 수량 0이면 보유 주식 삭제

---

## ✅ Validation & Exception Handling

- `@Valid` + Bean Validation 적용
- Controller 단계에서 입력값 검증
- `GlobalExceptionHandler`를 통한 예외 응답 통합 처리

예시 응답:
```json
{
  "result": 1,
  "code": 4000,
  "message": "stockQuantity : stockQuantity는 1 이상이어야 합니다."
}




📑 API Documentation (Swagger)

서버 실행 후 아래 주소에서 Swagger UI 확인 가능:

http://localhost:8080/swagger-ui/index.html


Swagger UI에서 로그인 후
쿠키가 자동으로 유지되어 매수/매도 테스트가 가능합니다.


▶️ How to Run
./mvnw clean spring-boot:run


Base URL

http://localhost:8080

🧪 Test Flow (추천 순서)

주식 등록

플레이어 생성

플레이어 로그인

주식 매수

플레이어 정보 조회

주식 매도

(Postman 또는 Swagger UI 사용)

🧠 What I Learned

Spring Boot 프로젝트 계층 구조 이해

JPA 연관관계(Entity) 설계

JWT + Cookie 기반 인증 흐름

Validation을 Controller에서 처리하는 이유

공통 Response / 예외 처리 구조 설계

Postman / Swagger UI를 활용한 API 테스트

🔧 Future Improvements

H2 메모리 DB → 파일 DB 전환

Swagger 문서 고도화

테스트 코드(JUnit) 추가

권한(Role) 기반 인증 확장

👤 Author

김도연

Industrial Engineering Major

Interested in Backend / Data / AI Systems

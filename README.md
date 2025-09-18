# OneYou - 일본 유학 온라인 학습 플랫폼

## 📚 프로젝트 개요

**OneYou**는 일본 유학을 준비하는 학생들을 위한 **EJU(일본유학시험) 대비 온라인 학습 플랫폼**입니다.

### 🎯 프로젝트 목적
- 일본 EJU 시험 준비를 위한 체계적인 학습 환경 제공
- 선생님과 학생 간의 효율적인 소통 및 피드백 시스템 구축
- 실시간 상호작용을 통한 능동적 학습 경험 제공

### ✨ 주요 기능
- **강의 관리**: 강의 생성, 챕터별 세부 강의 관리
- **과제 시스템**: 학생 과제 제출 및 선생님 피드백
- **진도 관리**: 학생별 학습 진도 추적 및 완료도 관리
- **실시간 채팅**: WebSocket 기반 실시간 소통
- **퀴즈 시스템**: 학습 평가 및 자가진단
- **사용자 관리**: JWT 기반 인증 및 역할별 권한 관리

### 👥 대상 사용자
- **학생**: EJU 시험 준비생, 일본 유학 준비생
- **선생님**: EJU 강의 진행 교사, 과제 피드백 제공자

---

## 🛠 기술 스택

### Backend
- **Framework**: Spring Boot 3.4.4
- **Language**: Java 21
- **Build Tool**: Gradle

### Database
- **RDBMS**: PostgreSQL
- **ORM**: JOOQ (Type-safe SQL)

### Authentication & Security
- **Authentication**: JWT (JSON Web Token)
- **Session Storage**: Redis
- **Security**: Spring Security

### Communication
- **Real-time**: WebSocket (실시간 채팅)
- **API Documentation**: Swagger/OpenAPI 3

### Development Tools
- **IDE**: IntelliJ IDEA
- **Testing**: HTTP Client (IntelliJ)
- **Version Control**: Git
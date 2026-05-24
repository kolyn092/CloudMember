# 📅 CloudMember

Spring Boot 기반의 멤버 관리 REST API 서비스입니다.  
AWS 클라우드 인프라를 활용하여 **Stateless 아키텍처**를 구현하고, 고가용성 및 글로벌 성능 최적화까지 적용한 프로젝트입니다.

---

## 📄 API 요약

| Method | URL                             | Desc        |
|:-------|:--------------------------------|:------------|
| POST   | /api/members                    | 멤버 생성       |
| GET    | /api/members/{memberId}         | 특정 멤버 조회    |
| POST   | /api/members/{id}/profile-image | 프로필 이미지 업로드 |
| GET    | /api/members/{id}/profile-image | 프로필 이미지 조회  |

---

## ⚙️ 환경 설정

| Profile | Database       | AWS 연동                  |
|:--------|:---------------|:------------------------|
| local   | H2 (In-Memory) | 비활성화                    |
| prod    | MySQL (RDS)    | Parameter Store, S3 활성화 |

---

## 🧩 구현 내용

### LV 0 - 요금 폭탄 방지 AWS Budget 설정

#### AWS Budgets에서 월 예산을 $100 로 설정

![](https://github.com/user-attachments/assets/abbfe9cb-8f29-41b3-b8f5-9fa232e5cb3c)

#### 예산의 80% 도달 시 이메일 알림이 오도록 설정

![](https://github.com/user-attachments/assets/a9294cfc-36e7-4a26-a1c8-1235d01f8873)

### LV 1 - 네트워크 구축 및 핵심 기능 배포

#### 설정 완료된 EC2

![](https://github.com/user-attachments/assets/cb6680bb-0e6c-43b7-a6e3-85258f415fb9)

### LV 2 - DB 분리 및 보안 연결하기

#### Actuator Info 엔드포인트 URL

- http://3.35.206.57:8080/actuator/info

#### RDS 보안 그룹 스크린샷

![](https://github.com/user-attachments/assets/aacaae33-7f87-443d-9825-984f39a8a0b7)

### LV 3 - 프로필 사진 기능 추가와 권한 관리

#### 발급받은 Presigned URL 및 만료 시간

- [Presigned URL]()
- 만료 시간 : 0000/00/00 00:00:00

#### 접근 성공 스크린샷

![](https://github.com/user-attachments/assets/874ba260-4712-4541-9d6e-16d58970b38b)
![](https://github.com/user-attachments/assets/d52cb54b-53c0-459e-b6f8-b6931eadbc2a)

### LV 4 - Docker & CI/CD 파이프라인 구축

#### Github Actions 성공 이미지

![](https://github.com/user-attachments/assets/d7695296-3787-4101-8c07-692d2ba33e67)

#### EC2 터미널 이미지

![](https://github.com/user-attachments/assets/691b70fb-0156-4115-a91b-02c51b45d99a)
# 📅 CloudMember



#### 🔗 [개발 기록 링크](https://www.notion.so/361c4f021a368099a91eff9f76765e8f)

---

## 📄 API 요약

| Method | URL                     | Desc     |
|:-------|:------------------------|:---------|
| POST   | /api/members            | 멤버 생성    |
| GET    | /api/members/{memberId} | 특정 멤버 조회 |

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
# 35-APPJAM-SERVER-ROOMIE

```
룸메이트 때문에 고통스러운 쉐어하우스 생활, ROOMIE에서 해결해요! 🏡
35기 AND SOPT 앱잼 ROOMIE 프로젝트입니다.
```

# ⚒️ SERVER Developer
|조동현</br>[@mr8356](https://github.com/mr8356)|김나연</br>[@Yeonnies](https://github.com/Yeonnies)|
|:---:|:---:|
|<img src = "https://github.com/user-attachments/assets/1d6b599d-c0e3-436e-a0d6-ed331c8503e4" width ="300">|<img src = "https://avatars.githubusercontent.com/u/126739852?v=4" width ="300">|
|`SERVER LEAD`</br>`Architecture Design`|`SERVER FOLLOWER`</br>`Database Design`|
</br>

# 🏛️ Project Structure (Multi-server)
|Module|Description|
|:---:|:---:|
|**common**|공통 엔티티와 DTO를 정의하는 모듈. 멀티 서버 환경에서 두 서버에 복제되어 공통 기능을 제공.|
|**producer**|클라이언트의 요청을 처리하고, 입주 신청 등의 데이터를 메시지 큐에 비동기로 삽입하여 생산자 역할을 수행.|
|**consumer**|메시지 큐에서 데이터를 소비하고 비동기로 연산을 처리하는 모듈. 주로 소비자 역할을 수행.|
</br>
</br>

# 📄 Infra Diagram (EDA: Event-Driven Architecture)
![Roomie_infraDiagram-Infra drawio (7)](https://github.com/user-attachments/assets/647906e5-8a55-44f5-a02e-1fa18337cdd0)
</br>
</br>

# 🖋️ Design Class Diagram (Initial Version)
![Roomie_infraDiagram-DCD drawio (5)](https://github.com/user-attachments/assets/c595aeaf-d088-4167-9861-a6fc19ea5713)
</br>
</br>

# 🌀 Library
|library|description|version|
|:---:|:---:|:---:|
|**Spring Boot**|Spring 기반 웹 애플리케이션 및 RESTful 서비스 개발을 위한 프레임워크|`3.2.1`|
|**Lombok**|클래스의 보일러플레이트 코드를 줄이기 위한 어노테이션 라이브러리|`1.18.24`|
|**Spring Data JPA**|JPA 기반 데이터베이스 작업을 보다 간편하게 할 수 있도록 지원|`3.2.1`|
|**Hibernate**|JPA 구현체로 객체-관계 매핑을 위한 라이브러리|`6.2.8.Final`|
|**MySQL Connector/J**|MySQL 데이터베이스와 연결하기 위한 JDBC 드라이버|`8.0.32`|
|**H2 Database**|경량화된 임베디드 데이터베이스, 주로 테스트 환경에서 사용|`2.1.214`|
|**JUnit 5**|자바의 단위 테스트를 위한 테스트 프레임워크|`5.10.0`|
|**Lettuce Client**|Redis와의 연결을 위한 클라이언트 라이브러리|`6.2.3`|
|**Mockito**|단위 테스트에서 의존성 주입을 모킹하여 테스트를 지원하는 라이브러리|`5.5.0`|
|**Spring Test**|Spring 애플리케이션 테스트를 위한 라이브러리|`3.2.1`|
|**ngrinder**|부하테스트 라이브러리|`3.2.1`|
</br>

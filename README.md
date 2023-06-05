# JWT기반 게시판 API 서버
# 👋소개
* Session기반 인증/인가 구조를 개선하기 위해 JWT 인증/인가 기능을 추가한 게시판 API서버입니다.
<br>

# 👉주요 기능
 * [핵심구현 요소] <span style="color:red">**Spring Security + JWT 기반 사용자 인증/인가 제한**</span>
    * Spring Security 기본 제공 세션기반 인증/인가 매커니즘 비활성화 및 JWT 인증/인가 필터 등록
    * 핵심 구조 : `ErrorHandlingFilter` -> `JwtAuthorizationFilter` -> `JwtAuthenticationFilter` -> other Spring security filter
    * `JwtAuthenticationFilter`는 `UsernamePasswordAuthenticationFilter`를 상속하여 기존 인증과정을 이용하되 인증성공 시 JWT토큰 발급
 * [핵심구현 요소] <span>**Spring RestDocs 기반 API문서 생성 자동화**</span>
    * 테스트 코드 기반 API문서 생성
    * RestDocs 공식 Reference문서 개선 작업 기여([Spring RestDocs issue](https://github.com/spring-projects/spring-restdocs/issues/892))
 * 게시글 및 댓글 작성/조회/수정/삭제
 * 게시글 좋아요
 * AWS S3 파일 업로드

<br>

# 👉API 문서 [링크]
 * Spring RestDocs 기반 API 문서 제공

![예시](https://github.com/JSCODE-EDU/project-class-galmegiz/assets/126640838/446259a5-6cf8-44ae-9cd9-9a08000a2d02)

<br>

# ⚙️세부 기술스택
 * Spring FrameWork
   * Spring Web
   * Spring Data Jpa
   * Spring Security
   * Spring RestDocs
* MySql
* Junit5
* AWS(EC2, S3)



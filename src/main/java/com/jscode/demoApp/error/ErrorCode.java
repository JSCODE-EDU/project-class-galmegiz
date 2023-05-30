package com.jscode.demoApp.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    SEVER_GLOBAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR , "G-A-001", "서버에 알 수 없는 오류가 발생했습니다."), //Global All Error : 특정 서비스에 종속되지 않는 Global Error
    UNAUTHORIZED_RESOURCE_ACCESS(HttpStatus.UNAUTHORIZED , "G-A-002", "인가되지 않은 자원으로 접근하였습니다."), //Global All Error2 : 인가되지 않은 자원으로의 접근
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "A-S-001", "게시글이 존재하지 않습니다."), //Article Service Error : 게시글이 존재하지 않음
    INVALID_REQUEST_ENCODE(HttpStatus.BAD_REQUEST, "A-C-001", "JSON 형식 요청이 아닙니다."), //Article Controller Error1 : Request 인코딩 형식 오류
    REQUEST_FIELD_ERROR(HttpStatus.BAD_REQUEST, "A-C-002", "입력 필드에 오류가 있습니다."), //Article Controller Error2 : Request 필드 검증 실패
    Member_Duplicate_Error(HttpStatus.BAD_REQUEST, "M-S-001", "중복된 사용자입니다."), //Member Service Error1 : 중복된 사용자 생성
    Login_Fail(HttpStatus.BAD_REQUEST, "M-S-002", "아이디가 없거나 패스워드가 일치하지 않습니다."), //Member Service Error2 : 로그인 실패
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M-S-003", "회원 정보가 존재하지 않습니다."), //Member Service Error3 : 회원id에 해당하는 정보가 존재하지 않음
    JWT_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "M-C-001", "인증 정보에 오류가 있습니다."),
    COMMENT_CREATE_ERROR(HttpStatus.BAD_REQUEST, "C-S-001", "댓글 작성에 실패하였습니다."); //Article Service Error1 : 댓글 생성에 필요한 정보가 부족함

    private HttpStatus status;
    private String code;
    private String message;

    ErrorCode(HttpStatus status, String code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }

}

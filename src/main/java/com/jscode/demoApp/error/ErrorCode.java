package com.jscode.demoApp.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "A-S-001", "게시글이 존재하지 않습니다."), //Article Service Error : 게시글이 존재하지 않음
    INVALID_REQUEST_ENCODE(HttpStatus.BAD_REQUEST, "A-C-001", "JSON 형식 요청이 아닙니다."), //Article Controller Error1 : Request 인코딩 형식 오류
    REQUEST_FIELD_ERROR(HttpStatus.BAD_REQUEST, "A-C-002", "입력 필드에 오류가 있습니다."); //Article Controller Error2 : Request 필드 검증 실패
    private HttpStatus status;
    private String code;
    private String message;

    ErrorCode(HttpStatus status, String code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }

}

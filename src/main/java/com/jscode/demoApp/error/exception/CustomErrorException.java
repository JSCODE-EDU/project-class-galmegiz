package com.jscode.demoApp.error.exception;

import com.jscode.demoApp.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class CustomErrorException extends RuntimeException {
    ErrorCode errorCode;

    public CustomErrorException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return this.errorCode;
    }
    public String getCode(){
        return this.errorCode.getCode();
    }

    public String getMessage(){
        return this.errorCode.getMessage();
    }

    public HttpStatus getStatus(){
        return this.errorCode.getStatus();
    }
}

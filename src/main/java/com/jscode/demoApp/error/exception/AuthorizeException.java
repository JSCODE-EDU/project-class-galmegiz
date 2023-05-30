package com.jscode.demoApp.error.exception;

import com.jscode.demoApp.error.ErrorCode;

public class AuthorizeException extends CustomErrorException{
    String resourceType;
    public AuthorizeException(ErrorCode errorCode){
        super(errorCode);
    }
    public AuthorizeException(ErrorCode errorCode, String resourceType){
        super(errorCode);
        this.resourceType = resourceType;
    }
}

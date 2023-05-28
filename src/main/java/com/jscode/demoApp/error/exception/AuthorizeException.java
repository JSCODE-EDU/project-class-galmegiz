package com.jscode.demoApp.error.exception;

import com.jscode.demoApp.error.ErrorCode;

public class AuthorizeException extends CustomErrorException{
    public AuthorizeException(ErrorCode errorCode){
        super(errorCode);
    }
}

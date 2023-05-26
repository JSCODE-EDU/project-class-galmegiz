package com.jscode.demoApp.error.exception;

import com.jscode.demoApp.error.ErrorCode;

public class ResourceNotFoundException extends CustomErrorException {
    public ResourceNotFoundException(ErrorCode errorCode){
        super(errorCode);
    }
}

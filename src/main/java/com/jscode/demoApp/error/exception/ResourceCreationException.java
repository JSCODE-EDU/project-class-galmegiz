package com.jscode.demoApp.error.exception;

import com.jscode.demoApp.error.ErrorCode;
import lombok.Getter;

@Getter
public class ResourceCreationException extends CustomErrorException{
    private String resourceName;

    public ResourceCreationException(ErrorCode errorCode, String resourceName) {
        super(errorCode);
        this.resourceName = resourceName;
    }
}

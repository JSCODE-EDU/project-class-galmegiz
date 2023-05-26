package com.jscode.demoApp.error.exception;

import com.jscode.demoApp.error.ErrorCode;
import org.springframework.validation.BindingResult;

public class FieldBindingException extends CustomErrorException{
    BindingResult bindingResult;
    public FieldBindingException(ErrorCode errorCode, BindingResult bindingResult){
        super(errorCode);
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}

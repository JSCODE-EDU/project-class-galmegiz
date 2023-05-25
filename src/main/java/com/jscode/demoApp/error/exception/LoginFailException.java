package com.jscode.demoApp.error.exception;

import com.jscode.demoApp.error.ErrorCode;

public class LoginFailException extends CustomErrorException{
    public LoginFailException() {
        super(ErrorCode.Login_Fail);
    }
}

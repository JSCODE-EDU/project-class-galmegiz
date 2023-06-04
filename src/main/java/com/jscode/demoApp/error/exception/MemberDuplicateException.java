package com.jscode.demoApp.error.exception;

import com.jscode.demoApp.error.ErrorCode;

public class MemberDuplicateException extends CustomErrorException{
    private final String duplicatedEmail;
    public MemberDuplicateException(String email) {
        super(ErrorCode.Member_Duplicate_Error);
        this.duplicatedEmail = email;
    }

    public String getDuplicatedEmail(){
        return this.duplicatedEmail;
    }

}

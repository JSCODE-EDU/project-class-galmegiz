package com.jscode.demoApp.dto.response;

import com.jscode.demoApp.error.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class ErrorResponseDto {
    private String code;
    private String message;
    private Object messageDetail;

    private ErrorResponseDto(String code, String message, Object messageDetail){
        this.code = code;
        this.message = message;
        this.messageDetail = messageDetail;
    }

    public static ErrorResponseDto of(ErrorCode errorCode){
        return ErrorResponseDto.of(errorCode, null);
    }

    public static ErrorResponseDto of(ErrorCode errorCode, Object messageDetail){
        return new ErrorResponseDto(errorCode.getCode(), errorCode.getMessage(), messageDetail);
    }
}

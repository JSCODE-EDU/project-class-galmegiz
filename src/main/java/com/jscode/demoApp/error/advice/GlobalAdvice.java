package com.jscode.demoApp.error.advice;

import com.jscode.demoApp.dto.response.ErrorResponseDto;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.FieldBindingException;
import com.jscode.demoApp.error.exception.MemberDuplicateException;
import com.jscode.demoApp.error.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice
public class GlobalAdvice {
    //@RequestBody에 text/html형식 데이터가 올 때
    /*HttpMessageConverter에서 throw하는 Exception이다보니 Custom Exception을 적용할 수 없다.
      그러다보니 위 메소드와 동일한 방식으로 처리 제한
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity messageNotReadableExHandler(HttpMessageNotReadableException ex){
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorCode.REQUEST_FIELD_ERROR);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity bindingExExHandler(BindException ex){
        /*
            bindingResult의 값을 errors Map에 담아 응답으로 보내준다.
            {에러 필드명1 : {에러 정보1, 에러 정보 2}, 에러 필드명2: {에러 정보1, 에러 정보2}라는
            형식으로 json응답을 보내고 싶을 때 Map을 쓰지 앟는 더 적절한 방법이 있을까?
            컨트롤러에서 여러 번 사용될 것 같으므로 별도 코드로 빼는 게 더 좋을 듯 하다.
         */

        Map<String, List<String>> messageDetail = new HashMap<>();
        BindingResult bindingResult = ex.getBindingResult();

        if(bindingResult.hasErrors()){
            bindingResult.getFieldErrors()
                    .forEach(e -> messageDetail
                            .computeIfAbsent(e.getField(), key -> new ArrayList<String>())
                            .add(e.getDefaultMessage()));
        }

        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorCode.INVALID_REQUEST_ENCODE, messageDetail);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity MethodArgumentNotValidExHandler(MethodArgumentNotValidException ex){
        /*
            bindingResult의 값을 errors Map에 담아 응답으로 보내준다.
            {에러 필드명1 : {에러 정보1, 에러 정보 2}, 에러 필드명2: {에러 정보1, 에러 정보2}라는
            형식으로 json응답을 보내고 싶을 때 Map을 쓰지 앟는 더 적절한 방법이 있을까?
            컨트롤러에서 여러 번 사용될 것 같으므로 별도 코드로 빼는 게 더 좋을 듯 하다.
         */

        Map<String, List<String>> messageDetail = new HashMap<>();
        BindingResult bindingResult = ex.getBindingResult();

        if(bindingResult.hasErrors()){
            bindingResult.getFieldErrors()
                    .forEach(e -> messageDetail
                            .computeIfAbsent(e.getField(), key -> new ArrayList<String>())
                            .add(e.getDefaultMessage()));
        }

        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorCode.REQUEST_FIELD_ERROR, messageDetail);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }



    @ExceptionHandler(FieldBindingException.class)
    public ResponseEntity FieldBindingExExHandler(FieldBindingException ex){
        /*
            bindingResult의 값을 errors Map에 담아 응답으로 보내준다.
            {에러 필드명1 : {에러 정보1, 에러 정보 2}, 에러 필드명2: {에러 정보1, 에러 정보2}라는
            형식으로 json응답을 보내고 싶을 때 Map을 쓰지 앟는 더 적절한 방법이 있을까?
            컨트롤러에서 여러 번 사용될 것 같으므로 별도 코드로 빼는 게 더 좋을 듯 하다.
         */

        Map<String, List<String>> messageDetail = new HashMap<>();
        BindingResult bindingResult = ex.getBindingResult();
        if(bindingResult.hasErrors()){
            bindingResult.getFieldErrors()
                    .forEach(e -> messageDetail
                            .computeIfAbsent(e.getField(), key -> new ArrayList<String>())
                            .add(e.getDefaultMessage()));
        }

        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorCode.INVALID_REQUEST_ENCODE, messageDetail);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    //검색 타입을 잘못지정하였을 때
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public void illegalArgumentExHandler(IllegalArgumentException ex){

        throw new IllegalStateException("유효하지 않은 입력입니다.");
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity globalExHandler(Throwable ex){
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorCode.SEVER_GLOBAL_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    //@ResponseStatus(HttpStatus.NOT_FOUND) annotation이 있더라도 response에서 상태코드를 지정하면 response 상태 코드가 우선임
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity resourceNotFountExHandler(ResourceNotFoundException ex){

        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ex.getErrorCode());
        return ResponseEntity.status(ex.getStatus()).body(Optional.of(errorResponseDto));
    }

    @ExceptionHandler(MemberDuplicateException.class)
    public ResponseEntity memberDupExHandler(MemberDuplicateException ex){
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(ErrorCode.Member_Duplicate_Error, ex.getDuplicatedEmail());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }
}

package com.jscode.demoApp.error.advice;

import com.jscode.demoApp.controller.ArticleController;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice(assignableTypes = ArticleController.class)
public class ArticleAdvice {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public String entityExHandler(EntityNotFoundException ex){
        //todo: API인 점을 고려해서 문자열보다는 부호화된 코드를 보내는 걸로 refactor필요
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String typeMismatchHandler(HttpMessageNotReadableException ex){

        return "JSON형식으로 보내주세요.";
    }
}

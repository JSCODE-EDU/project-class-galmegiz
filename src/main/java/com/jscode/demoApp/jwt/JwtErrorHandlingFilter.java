package com.jscode.demoApp.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jscode.demoApp.dto.response.ErrorResponseDto;
import com.jscode.demoApp.error.ErrorCode;
import com.jscode.demoApp.error.exception.LoginFailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtErrorHandlingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();

        try{
            filterChain.doFilter(request, response);
        }catch(LoginFailException e){
            String body = mapper.writeValueAsString(ErrorResponseDto.of(e.getErrorCode()));

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(body);
        }catch(IllegalArgumentException e){
            String body = mapper.writeValueAsString(ErrorResponseDto.of(ErrorCode.INVALID_REQUEST_ENCODE));

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(body);
        }
    }
}

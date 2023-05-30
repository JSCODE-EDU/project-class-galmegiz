package com.jscode.demoApp.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jscode.demoApp.dto.request.LoginRequest;
import com.jscode.demoApp.error.exception.LoginFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        log.info("login filter");

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        LoginRequest loginRequest = null;
        try {
            loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.getEmail(), loginRequest.getPassword());
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        } catch (AuthenticationException e){
            throw new LoginFailException();
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("successfulAuthentication");
        SecurityContextHolder.getContext().setAuthentication(authResult);

        String jwtToken = jwtTokenProvider.createToken(authResult);

        response.addHeader(jwtTokenProvider.AUTHORIZATION_HEADER,JwtTokenProvider.HEADER_PREFIX + " " + jwtToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print("로그인 성공");
        response.setStatus(200);
    }

    /*
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = ((HttpServletRequest) request).getHeader("Authorization");

        //인증 토큰이 정상인 경우 Security Context에 인증정보를 담고
        /*FilterChainProxy -> AbstractAuthenticationProcessingFilter#doFilter
            -> JwtAuthenticationFilter#attemptAuthentication(request, response) (인증과정 구현 필요)
            -> JwtAuthenticationFilter#successfulAuthentication(jwt 토큰 발행하고 끝)



        if(token != null && jwtTokenProvider.validateToken(token)){
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
    */


}

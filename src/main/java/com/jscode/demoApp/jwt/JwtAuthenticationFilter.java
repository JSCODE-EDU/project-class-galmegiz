package com.jscode.demoApp.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jscode.demoApp.dto.UserPrincipal;
import com.jscode.demoApp.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;


    //doFilter에서 requiresAuthentication requiresAuthenticationRequestMatcher(default /login)과 request가 일치하지 않으면
    //attempAuthentication 수행하지 않고 doFilter로 넘어감
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        String token = ((HttpServletRequest) request).getHeader("Authrorization");
//        log.info(token);
//
//        if(token != null && jwtTokenProvider.validateToken(token)){
//            Authentication authentication = jwtTokenProvider.getAuthentication(token);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            chain.doFilter(request, response);
//        }else{
//            ((AbstractAuthenticationProcessingFilter)this).doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
//        }
//    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException{
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String userName = request.getParameter("userName");
        userName = (userName != null) ? userName.trim() : "";
        String password = request.getParameter("password");
        password = (password != null) ? password.trim() : "";
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(userName, password);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("successfulAuthentication");
        SecurityContextHolder.getContext().setAuthentication(authResult);

        String jwtToken = jwtTokenProvider.createToken(authResult);
        ObjectMapper objectMapper = new ObjectMapper();
        response.addHeader(jwtTokenProvider.AUTHORIZATION_HEADER,"Bearer " + jwtToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println("로그인 성공");
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

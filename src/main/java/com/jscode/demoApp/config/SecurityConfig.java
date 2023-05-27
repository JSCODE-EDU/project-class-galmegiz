package com.jscode.demoApp.config;

import com.jscode.demoApp.dto.MemberDto;
import com.jscode.demoApp.jwt.JwtAuthenticationFilter;
import com.jscode.demoApp.dto.UserPrincipal;
import com.jscode.demoApp.error.exception.LoginFailException;
import com.jscode.demoApp.jwt.JwtAuthorizationFilter;
import com.jscode.demoApp.service.MemberService;
import com.jscode.demoApp.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests(request -> request.mvcMatchers("/members/**").permitAll());

        return http.build();
    }

   @Bean
   public UserDetailsService userDetailsService(MemberService memberService){
     return email -> UserPrincipal.fromDto(memberService.findMemberByEmail(email));
    }
}

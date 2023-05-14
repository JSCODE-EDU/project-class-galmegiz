package com.jscode.demoApp.config;

import com.jscode.demoApp.error.resolver.CustomExceptionResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /* 현재 미사용
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }*/

    /* @ExceptionHandler를 사용하므로 customResolver는 사용하지 않기로 하였음.
    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new CustomExceptionResolver());
    }
    */

}

package com.jscode.demoApp.error.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomExceptionResolver implements HandlerExceptionResolver {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler, Exception ex) {
        try {
            if(ex instanceof EntityNotFoundException) {
                log.info("Entity Not Found Exception");
                String acceptHeader = request.getHeader("accept");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                if(MediaType.APPLICATION_JSON_VALUE.equals(acceptHeader)){
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());
                    String result = objectMapper.writeValueAsString(errorResult);

                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().println(result);
                    return new ModelAndView();
                } else{
                    //todo : 에러 뷰페이지 없음
                    return new ModelAndView("error/500");
                }
            }
        }catch (IOException e) {
            log.error("ExceptionResolver Error : {}", e.getMessage());
        }

        return null;
    }
}

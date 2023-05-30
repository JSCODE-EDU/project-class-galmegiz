package com.jscode.demoApp.controller.validator;

import com.jscode.demoApp.constant.SearchType;
import com.jscode.demoApp.dto.request.SearchRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Slf4j
public class SearchValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        log.info("customvalidator : {}", clazz.getClass());
        return SearchRequestDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SearchRequestDto searchRequestDto = (SearchRequestDto) target;
        log.info("customvalidator : {}", searchRequestDto);
        if(searchRequestDto.getSearchType() != null){
            if(searchRequestDto.getSearchKeyword() == null || searchRequestDto.getSearchKeyword().length() < 1){
                errors.rejectValue("searchKeyword", "Size" ,"검색 키워드는 1글자 이상입니다.");
            }
        }
    }
}

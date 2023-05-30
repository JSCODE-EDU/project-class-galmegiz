package com.jscode.demoApp.dto.request;


public record PageRequest(Integer page,
                          Integer size) {


    public PageRequest{
        if( page == null || size == null){
            page = 0;
            size = 1;
        }
    }
}

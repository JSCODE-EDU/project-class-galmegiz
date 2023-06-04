package com.jscode.demoApp.dto.request;

import com.jscode.demoApp.constant.SearchType;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
public class SearchRequestDto {

    SearchType searchType;

    String searchKeyword;
}

package com.jscode.demoApp.dto.request;

import com.jscode.demoApp.constant.SearchType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
public class SearchRequestDto {

    SearchType searchType;

    String searchKeyword;
}

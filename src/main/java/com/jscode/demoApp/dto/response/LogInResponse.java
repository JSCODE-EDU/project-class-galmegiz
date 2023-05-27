package com.jscode.demoApp.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class LogInResponse {
    @Setter String token;
}

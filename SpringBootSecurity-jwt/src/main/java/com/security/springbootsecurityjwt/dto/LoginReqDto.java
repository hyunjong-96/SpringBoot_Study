package com.security.springbootsecurityjwt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginReqDto {
	private String email;
	private String password;
}

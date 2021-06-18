package com.security.springbootsecurityjwt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TokensResDto {
	private String accessToken;
	private String refreshToken;

	public TokensResDto(String accessToken, String refreshToken){
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}

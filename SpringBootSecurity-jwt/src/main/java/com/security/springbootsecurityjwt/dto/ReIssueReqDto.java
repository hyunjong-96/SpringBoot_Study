package com.security.springbootsecurityjwt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReIssueReqDto {
	private String accessToken;
	private String refreshToken;

	@Builder
	public ReIssueReqDto(String accessToken,String refreshToken){
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}

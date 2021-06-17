package com.security.springbootsecurityjwt.dto;

import com.security.springbootsecurityjwt.domain.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GetUserInfoResDto {
	private Long id;

	private String name;

	private String email;

	private String password;

	@Builder
	public GetUserInfoResDto(User user){
		id = user.getId();
		name = user.getName();
		email = user.getEmail();
		password = user.getPassword();
	}
}

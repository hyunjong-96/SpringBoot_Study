package com.security.springbootsecurityjwt.dto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.security.springbootsecurityjwt.domain.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RegistrationReqDto {
	private String name;
	private String email;
	private String password;

	@Builder
	public RegistrationReqDto(String name, String email, String password){
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public User toEntity(){
		return User.builder()
			.name(name)
			.email(email)
			.password(new BCryptPasswordEncoder().encode(password))
			.build();
	}
}

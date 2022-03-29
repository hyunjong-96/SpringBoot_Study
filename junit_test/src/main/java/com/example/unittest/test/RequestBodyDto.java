package com.example.unittest.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class RequestBodyDto {
	private String name;
	private long age;
	private String password;
	private String email;

	public RequestBodyDto(){}

	public RequestBodyDto(
		String name,
		long age,
		String password,
		String email
	){
		this.name = name;
		this.age = age;
		this.password = password;
		this.email = email;
	}
}

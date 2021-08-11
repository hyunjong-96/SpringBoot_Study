package com.example.querydsl.user;

import com.example.querydsl.user.doamin.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto{
	private String name;
	private Integer age;

	public User toEntity(){
		return User.builder()
			.name(name)
			.age(age)
			.build();
	}
}

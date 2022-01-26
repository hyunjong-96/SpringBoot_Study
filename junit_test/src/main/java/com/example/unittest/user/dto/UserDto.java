package com.example.unittest.user.dto;

import com.example.unittest.user.Role;
import com.example.unittest.user.Trainer;
import com.example.unittest.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private String name;
	private Integer age;
	private Role role;

	public User toEntity(){
		return Trainer.builder()
			.name(name)
			.age(age)
			.role(role)
			.build();
	}
}

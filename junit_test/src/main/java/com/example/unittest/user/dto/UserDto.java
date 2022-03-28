package com.example.unittest.user.dto;

import com.example.unittest.user.Role;
import com.example.unittest.user.Trainer;
import com.example.unittest.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class UserDto {
	private String name;
	private Integer age;
	private Role role;

	@Builder
	public UserDto(String name, Integer age, Role role){
		this.name = name;
		this.age = age;
		this.role = role;
	}

	public User toEntity(){
		return Trainer.builder()
			.name(name)
			.age(age)
			.role(role)
			.build();
	}
}

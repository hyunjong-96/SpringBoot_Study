package com.example.querydsl.user;

import java.util.List;
import java.util.stream.Collectors;

import com.example.querydsl.rutin.domain.Rutin;
import com.example.querydsl.user.doamin.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GetUserDto {
	private Long id;
	private String name;
	private Integer age;
	private List<RutinDto> rutinDtos;

	public GetUserDto(User user){
		this.id = user.getId();
		this.name = user.getName();
		this.age = user.getAge();
		this.rutinDtos = user.getRutins().stream().map(RutinDto::new).collect(Collectors.toList());
	}
}

@NoArgsConstructor
@Getter
class RutinDto{
	private Long id;
	private String part;

	public RutinDto(Rutin rutin){
		this.id = rutin.getId();
		this.part = rutin.getPart();
	}
}

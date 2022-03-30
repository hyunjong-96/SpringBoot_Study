package com.example.unittest.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
// @Setter
@AllArgsConstructor
public class ModelAttributeDto {
	private String name;
	private long age;
	private String password;
	private String email;

	// public ModelAttributeDto(
	// 	String name,
	// 	long age,
	// 	String password,
	// 	String email) {
	//
	// 	this.name = name;
	// 	this.age = age;
	// 	this.password = password;
	// 	this.email = email;
	// }
}
